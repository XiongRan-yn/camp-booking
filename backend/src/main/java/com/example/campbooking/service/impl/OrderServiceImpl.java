package com.example.campbooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.campbooking.common.BusinessException;
import com.example.campbooking.common.PageResult;
import com.example.campbooking.dto.OrderCalculateRequest;
import com.example.campbooking.dto.OrderCreateRequest;
import com.example.campbooking.entity.Coupon;
import com.example.campbooking.entity.Order;
import com.example.campbooking.entity.PointsRecord;
import com.example.campbooking.entity.Product;
import com.example.campbooking.entity.ProductSpec;
import com.example.campbooking.entity.User;
import com.example.campbooking.entity.UserCoupon;
import com.example.campbooking.mapper.CouponMapper;
import com.example.campbooking.mapper.OrderMapper;
import com.example.campbooking.mapper.PointsRecordMapper;
import com.example.campbooking.mapper.ProductMapper;
import com.example.campbooking.mapper.ProductSpecMapper;
import com.example.campbooking.mapper.UserCouponMapper;
import com.example.campbooking.mapper.UserMapper;
import com.example.campbooking.service.OrderService;
import com.example.campbooking.vo.OrderVO;
import com.example.campbooking.vo.UserCouponVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final BigDecimal POINTS_RATE = new BigDecimal("100");
    private static final BigDecimal MAX_POINTS_RATIO = new BigDecimal("0.10");

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final ProductSpecMapper specMapper;
    private final UserMapper userMapper;
    private final UserCouponMapper userCouponMapper;
    private final CouponMapper couponMapper;
    private final PointsRecordMapper pointsRecordMapper;

    public OrderServiceImpl(OrderMapper orderMapper,
                            ProductMapper productMapper,
                            ProductSpecMapper specMapper,
                            UserMapper userMapper,
                            UserCouponMapper userCouponMapper,
                            CouponMapper couponMapper,
                            PointsRecordMapper pointsRecordMapper) {
        this.orderMapper = orderMapper;
        this.productMapper = productMapper;
        this.specMapper = specMapper;
        this.userMapper = userMapper;
        this.userCouponMapper = userCouponMapper;
        this.couponMapper = couponMapper;
        this.pointsRecordMapper = pointsRecordMapper;
    }

    @Override
    public Map<String, Object> calculate(Long userId, OrderCalculateRequest request) {
        ProductSpec spec = validateProductAndSpec(request.getProductId(), request.getSpecId());
        int quantity = normalizeQuantity(request.getQuantity());
        validateStock(spec, quantity);

        BigDecimal unitPrice = spec.getPrice();
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));

        BigDecimal couponDiscount = BigDecimal.ZERO;
        if (request.getCouponId() != null) {
            Coupon coupon = getUsableCouponForOrder(userId, request.getCouponId(), totalPrice);
            couponDiscount = calcCouponDiscount(coupon, totalPrice);
        }

        User user = userMapper.selectById(userId);
        List<UserCouponVO> availableCoupons = listUsableCoupons(userId, totalPrice);
        BigDecimal maxPointsDiscount = calcMaxPointsDiscount(user, totalPrice);

        BigDecimal actualPrice = totalPrice.subtract(couponDiscount);
        if (actualPrice.compareTo(BigDecimal.ZERO) < 0) {
            actualPrice = BigDecimal.ZERO;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("unitPrice", unitPrice);
        result.put("totalPrice", totalPrice);
        result.put("couponDiscount", couponDiscount);
        result.put("pointsDiscount", BigDecimal.ZERO);
        result.put("actualPrice", actualPrice);
        result.put("availableCoupons", availableCoupons);
        result.put("maxPointsDiscount", maxPointsDiscount);
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> create(Long userId, OrderCreateRequest request) {
        ProductSpec spec = validateProductAndSpec(request.getProductId(), request.getSpecId());
        Product product = productMapper.selectById(request.getProductId());
        int quantity = normalizeQuantity(request.getQuantity());
        validateStock(spec, quantity);

        if (request.getContactName() == null || request.getContactName().trim().isEmpty()) {
            throw new BusinessException(400, "联系人姓名不能为空");
        }
        if (request.getContactPhone() == null || request.getContactPhone().trim().isEmpty()) {
            throw new BusinessException(400, "联系人手机号不能为空");
        }

        BigDecimal unitPrice = spec.getPrice();
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));

        BigDecimal couponDiscount = BigDecimal.ZERO;
        UserCoupon usedUserCoupon = null;
        if (request.getCouponId() != null) {
            usedUserCoupon = getUsableUserCoupon(userId, request.getCouponId(), totalPrice);
            Coupon coupon = couponMapper.selectById(usedUserCoupon.getCouponId());
            couponDiscount = calcCouponDiscount(coupon, totalPrice);
        }

        BigDecimal pointsDiscount = BigDecimal.ZERO;
        int pointsUsed = request.getPointsUsed() != null ? request.getPointsUsed() : 0;
        if (pointsUsed > 0) {
            User user = userMapper.selectById(userId);
            if (user.getPointsBalance() == null || user.getPointsBalance() < pointsUsed) {
                throw new BusinessException(400, "积分余额不足");
            }
            pointsDiscount = BigDecimal.valueOf(pointsUsed).divide(POINTS_RATE, 2, java.math.RoundingMode.HALF_UP);
            BigDecimal maxDiscount = calcMaxPointsDiscount(user, totalPrice);
            if (pointsDiscount.compareTo(maxDiscount) > 0) {
                throw new BusinessException(400, "积分抵扣超过上限");
            }
        }

        BigDecimal actualPrice = totalPrice.subtract(couponDiscount).subtract(pointsDiscount);
        if (actualPrice.compareTo(BigDecimal.ZERO) < 0) {
            actualPrice = BigDecimal.ZERO;
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setProductId(product.getId());
        order.setSpecId(spec.getId());
        order.setSpecName(spec.getName());
        order.setQuantity(quantity);
        order.setUnitPrice(unitPrice);
        order.setTotalPrice(totalPrice);
        order.setCouponId(request.getCouponId());
        order.setCouponDiscount(couponDiscount);
        order.setPointsDiscount(pointsDiscount);
        order.setActualPrice(actualPrice);
        order.setContactName(request.getContactName().trim());
        order.setContactPhone(request.getContactPhone().trim());
        order.setRemark(request.getRemark() != null ? request.getRemark() : "");
        order.setStatus("pending");
        orderMapper.insert(order);

        if (usedUserCoupon != null) {
            usedUserCoupon.setStatus("used");
            usedUserCoupon.setUsedAt(LocalDateTime.now());
            userCouponMapper.updateById(usedUserCoupon);
        }

        if (pointsUsed > 0) {
            User user = userMapper.selectById(userId);
            user.setPointsBalance(user.getPointsBalance() - pointsUsed);
            userMapper.updateById(user);

            PointsRecord record = new PointsRecord();
            record.setUserId(userId);
            record.setAmount(-pointsUsed);
            record.setType("spend");
            record.setSource("order");
            record.setRemark("订单抵扣");
            pointsRecordMapper.insert(record);
        }

        deductStock(product, spec, quantity);

        Map<String, Object> result = new HashMap<>();
        result.put("id", order.getId());
        result.put("orderNo", order.getOrderNo());
        result.put("actualPrice", order.getActualPrice());
        result.put("status", order.getStatus());
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> pay(Long userId, Long orderId) {
        Order order = getOwnedOrder(userId, orderId);
        if (!"pending".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不正确，无法支付");
        }

        order.setStatus("paid");
        order.setPaidAt(LocalDateTime.now());
        orderMapper.updateById(order);

        int awarded = order.getActualPrice().multiply(POINTS_RATE).multiply(new BigDecimal("0.01")).intValue();
        if (awarded > 0) {
            User user = userMapper.selectById(userId);
            user.setPointsBalance((user.getPointsBalance() != null ? user.getPointsBalance() : 0) + awarded);
            userMapper.updateById(user);

            PointsRecord record = new PointsRecord();
            record.setUserId(userId);
            record.setAmount(awarded);
            record.setType("earn");
            record.setSource("order_pay");
            record.setRemark("订单支付奖励");
            pointsRecordMapper.insert(record);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("status", order.getStatus());
        result.put("paidAt", order.getPaidAt());
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> complete(Long userId, Long orderId) {
        Order order = getOwnedOrder(userId, orderId);
        if (!"paid".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不正确，无法完成");
        }

        order.setStatus("completed");
        order.setCompletedAt(LocalDateTime.now());
        orderMapper.updateById(order);

        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("status", order.getStatus());
        result.put("completedAt", order.getCompletedAt());
        return result;
    }

    @Override
    @Transactional
    public void cancel(Long userId, Long orderId) {
        Order order = getOwnedOrder(userId, orderId);
        if (!"pending".equals(order.getStatus())) {
            throw new BusinessException(400, "订单状态不正确，无法取消");
        }

        order.setStatus("cancelled");
        orderMapper.updateById(order);

        restoreStock(order);

        if (order.getCouponId() != null) {
            LambdaQueryWrapper<UserCoupon> ucWrapper = new LambdaQueryWrapper<>();
            ucWrapper.eq(UserCoupon::getUserId, userId);
            ucWrapper.eq(UserCoupon::getCouponId, order.getCouponId());
            ucWrapper.eq(UserCoupon::getStatus, "used");
            UserCoupon userCoupon = userCouponMapper.selectOne(ucWrapper);
            if (userCoupon != null) {
                userCoupon.setStatus("usable");
                userCoupon.setUsedAt(null);
                userCouponMapper.updateById(userCoupon);
            }
        }

        if (order.getPointsDiscount() != null && order.getPointsDiscount().compareTo(BigDecimal.ZERO) > 0) {
            int pointsUsed = order.getPointsDiscount().multiply(POINTS_RATE).intValue();
            User user = userMapper.selectById(userId);
            user.setPointsBalance((user.getPointsBalance() != null ? user.getPointsBalance() : 0) + pointsUsed);
            userMapper.updateById(user);

            PointsRecord record = new PointsRecord();
            record.setUserId(userId);
            record.setAmount(pointsUsed);
            record.setType("earn");
            record.setSource("order_cancel");
            record.setRemark("订单取消退还积分");
            pointsRecordMapper.insert(record);
        }
    }

    @Override
    public PageResult<OrderVO> list(Long userId, String status, String keyword, Integer page, Integer pageSize) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Order::getStatus, status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Order::getOrderNo, keyword);
        }
        wrapper.orderByDesc(Order::getCreatedAt);

        Page<Order> mpPage = new Page<>(page, pageSize);
        Page<Order> result = orderMapper.selectPage(mpPage, wrapper);

        Map<Long, Product> productMap = loadProductMap(result.getRecords());

        List<OrderVO> list = result.getRecords().stream()
                .map(o -> toOrderVO(o, productMap.get(o.getProductId())))
                .collect(Collectors.toList());

        return PageResult.of(list, result.getTotal(), page, pageSize);
    }

    @Override
    public OrderVO detail(Long userId, Long orderId) {
        Order order = getOwnedOrder(userId, orderId);
        Product product = productMapper.selectById(order.getProductId());
        return toOrderVO(order, product);
    }

    private ProductSpec validateProductAndSpec(Long productId, Long specId) {
        if (productId == null || specId == null) {
            throw new BusinessException(400, "商品和规格不能为空");
        }
        Product product = productMapper.selectById(productId);
        if (product == null || !"on".equals(product.getStatus())) {
            throw new BusinessException(404, "商品不存在或已下架");
        }
        ProductSpec spec = specMapper.selectById(specId);
        if (spec == null || !spec.getProductId().equals(productId)) {
            throw new BusinessException(400, "规格不存在");
        }
        return spec;
    }

    private int normalizeQuantity(Integer quantity) {
        if (quantity == null || quantity < 1) {
            return 1;
        }
        return quantity;
    }

    private void validateStock(ProductSpec spec, int quantity) {
        if (spec.getStock() == null || spec.getStock() < quantity) {
            throw new BusinessException(400, "库存不足");
        }
    }

    private Coupon getUsableCouponForOrder(Long userId, Long userCouponId, BigDecimal totalPrice) {
        UserCoupon userCoupon = getUsableUserCoupon(userId, userCouponId, totalPrice);
        return couponMapper.selectById(userCoupon.getCouponId());
    }

    private UserCoupon getUsableUserCoupon(Long userId, Long userCouponId, BigDecimal totalPrice) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null || !userCoupon.getUserId().equals(userId)) {
            throw new BusinessException(400, "优惠券不存在");
        }
        if (!"usable".equals(userCoupon.getStatus())) {
            throw new BusinessException(400, "优惠券不可用");
        }
        if (userCoupon.getExpireAt() != null && userCoupon.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(400, "优惠券已过期");
        }
        Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
        if (coupon != null && coupon.getMinAmount() != null && totalPrice.compareTo(coupon.getMinAmount()) < 0) {
            throw new BusinessException(400, "未达到优惠券使用门槛");
        }
        return userCoupon;
    }

    private BigDecimal calcCouponDiscount(Coupon coupon, BigDecimal totalPrice) {
        if (coupon == null) {
            return BigDecimal.ZERO;
        }
        if ("cash".equals(coupon.getType())) {
            BigDecimal discount = coupon.getDiscountValue();
            return discount.compareTo(totalPrice) > 0 ? totalPrice : discount;
        } else if ("discount".equals(coupon.getType())) {
            BigDecimal rate = BigDecimal.ONE.subtract(coupon.getDiscountValue());
            return totalPrice.multiply(rate);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calcMaxPointsDiscount(User user, BigDecimal totalPrice) {
        if (user == null || user.getPointsBalance() == null || user.getPointsBalance() <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal pointsValue = BigDecimal.valueOf(user.getPointsBalance()).divide(POINTS_RATE, 2, java.math.RoundingMode.DOWN);
        BigDecimal ratioCap = totalPrice.multiply(MAX_POINTS_RATIO);
        return pointsValue.compareTo(ratioCap) < 0 ? pointsValue : ratioCap;
    }

    private List<UserCouponVO> listUsableCoupons(Long userId, BigDecimal totalPrice) {
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);
        wrapper.eq(UserCoupon::getStatus, "usable");
        wrapper.gt(UserCoupon::getExpireAt, LocalDateTime.now());
        List<UserCoupon> list = userCouponMapper.selectList(wrapper);

        List<UserCouponVO> result = new ArrayList<>();
        for (UserCoupon uc : list) {
            Coupon coupon = couponMapper.selectById(uc.getCouponId());
            if (coupon == null || !"cash".equals(coupon.getType()) && !"discount".equals(coupon.getType())) {
                continue;
            }
            if (coupon.getMinAmount() != null && totalPrice.compareTo(coupon.getMinAmount()) < 0) {
                continue;
            }
            UserCouponVO vo = new UserCouponVO();
            vo.setId(uc.getId());
            vo.setCouponId(uc.getCouponId());
            vo.setCouponTitle(uc.getCouponTitle());
            vo.setType(coupon.getType());
            vo.setDiscountValue(coupon.getDiscountValue());
            vo.setMinAmount(coupon.getMinAmount());
            vo.setStatus(uc.getStatus());
            vo.setReceivedAt(uc.getReceivedAt());
            vo.setUsedAt(uc.getUsedAt());
            vo.setExpireAt(uc.getExpireAt());
            result.add(vo);
        }
        return result;
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = new Random().nextInt(900) + 100;
        return timestamp + random;
    }

    private Order getOwnedOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(404, "订单不存在");
        }
        return order;
    }

    private void deductStock(Product product, ProductSpec spec, int quantity) {
        spec.setStock(spec.getStock() - quantity);
        specMapper.updateById(spec);
        if (product.getStock() != null) {
            product.setStock(Math.max(0, product.getStock() - quantity));
            productMapper.updateById(product);
        }
    }

    private void restoreStock(Order order) {
        ProductSpec spec = specMapper.selectById(order.getSpecId());
        if (spec != null) {
            spec.setStock((spec.getStock() != null ? spec.getStock() : 0) + order.getQuantity());
            specMapper.updateById(spec);
        }
        Product product = productMapper.selectById(order.getProductId());
        if (product != null) {
            product.setStock((product.getStock() != null ? product.getStock() : 0) + order.getQuantity());
            productMapper.updateById(product);
        }
    }

    private Map<Long, Product> loadProductMap(List<Order> orders) {
        List<Long> productIds = orders.stream().map(Order::getProductId).distinct().collect(Collectors.toList());
        if (productIds.isEmpty()) {
            return new HashMap<>();
        }
        List<Product> products = productMapper.selectBatchIds(productIds);
        Map<Long, Product> map = new HashMap<>();
        for (Product p : products) {
            map.put(p.getId(), p);
        }
        return map;
    }

    private OrderVO toOrderVO(Order order, Product product) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setProductId(order.getProductId());
        if (product != null) {
            vo.setProductTitle(product.getTitle());
            vo.setProductImage(product.getCoverImage());
            vo.setCategory(product.getCategory());
        }
        vo.setSpecId(order.getSpecId());
        vo.setSpecName(order.getSpecName());
        vo.setQuantity(order.getQuantity());
        vo.setUnitPrice(order.getUnitPrice());
        vo.setTotalPrice(order.getTotalPrice());
        vo.setCouponDiscount(order.getCouponDiscount());
        vo.setPointsDiscount(order.getPointsDiscount());
        vo.setActualPrice(order.getActualPrice());
        vo.setContactName(order.getContactName());
        vo.setContactPhone(order.getContactPhone());
        vo.setRemark(order.getRemark());
        vo.setStatus(order.getStatus());
        vo.setPaidAt(order.getPaidAt());
        vo.setCompletedAt(order.getCompletedAt());
        vo.setCreatedAt(order.getCreatedAt());
        return vo;
    }
}

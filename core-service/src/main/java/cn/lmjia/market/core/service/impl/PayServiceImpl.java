package cn.lmjia.market.core.service.impl;

import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.request.PromotionRequest;
import cn.lmjia.market.core.entity.support.OrderStatus;
import cn.lmjia.market.core.entity.support.PaymentStatus;
import cn.lmjia.market.core.repository.MainOrderRepository;
import cn.lmjia.market.core.repository.PayOrderRepository;
import cn.lmjia.market.core.repository.request.PromotionRequestRepository;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.core.service.PayService;
import cn.lmjia.market.core.service.request.PromotionRequestService;
import lombok.SneakyThrows;
import me.jiangcai.payment.PayableOrder;
import me.jiangcai.payment.chanpay.entity.ChanpayPayOrder;
import me.jiangcai.payment.entity.PayOrder;
import me.jiangcai.payment.event.OrderPayCancellation;
import me.jiangcai.payment.event.OrderPaySuccess;
import me.jiangcai.wx.web.WeixinWebSpringConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author CJ
 */
@Service
public class PayServiceImpl implements PayService {

    private static final Log log = LogFactory.getLog(PayServiceImpl.class);

    @Autowired
    private MainOrderService mainOrderService;
    @Autowired
    private MainOrderRepository mainOrderRepository;
    @Autowired
    private PayOrderRepository payOrderRepository;
    @Autowired
    private PromotionRequestRepository promotionRequestRepository;
    @Autowired
    private PromotionRequestService promotionRequestService;

    @Override
    public ModelAndView paySuccess(HttpServletRequest request, PayableOrder payableOrder, PayOrder payOrder) {
        if (payableOrder instanceof MainOrder) {
            MainOrder mainOrder = (MainOrder) payableOrder;
            if (!WeixinWebSpringConfig.isWeixinRequest(request)) {
                // 非
                return new ModelAndView("redirect:/agentPaySuccess?mainOrderId=" + mainOrder.getId() + "&payOrderId=" + payOrder.getId());
            }
            return new ModelAndView("redirect:/wechatPaySuccess?mainOrderId=" + mainOrder.getId() + "&payOrderId=" + payOrder.getId());
        } else if (payableOrder instanceof PromotionRequest) {
            return new ModelAndView("redirect:/wechatUpgradeApplySuccess");
        } else
            throw new IllegalStateException("暂时不支持：" + payableOrder);
    }

    @Override
    @SneakyThrows
    public ModelAndView pay(HttpServletRequest request, PayableOrder order, PayOrder payOrder, Map<String, Object> additionalParameters) {
        if (!WeixinWebSpringConfig.isWeixinRequest(request)) {
            // 非微信下是直接跳转到收银台
            ChanpayPayOrder chanpayPayOrder = (ChanpayPayOrder) payOrder;
            return new ModelAndView("redirect:" + chanpayPayOrder.getUrl());
        }
        return new ModelAndView("redirect:/_pay/paying?payableOrderId=" + order.getPayableOrderId() + "&payOrderId="
                + payOrder.getId());
        //"&checkUri="
//        + URLEncoder.encode(additionalParameters.get("checkUri").toString(), "UTF-8") + "&successUri="
//                + URLEncoder.encode(additionalParameters.get("successUri").toString(), "UTF-8")
    }

    @Override
    public boolean isPaySuccess(String id) {
        // 前面的main-去去掉
        long orderId = toOrderId(id);
        Class<? extends PayableOrder> type = toOrderType(id);
        if (type == MainOrder.class)
            return mainOrderService.isPaySuccess(orderId);
        return promotionRequestRepository.getOne(orderId).getPaymentStatus() == PaymentStatus.payed;
    }

    private long toOrderId(String text) {
        return NumberUtils.parseNumber(text.split("-")[1], Long.class);
    }

    private Class<? extends PayableOrder> toOrderType(String text) {
        final String text1 = text.split("-")[0];
        if (text1.equalsIgnoreCase("main"))
            return MainOrder.class;
        if (text1.equalsIgnoreCase("PromotionRequest"))
            return PromotionRequest.class;
        throw new IllegalArgumentException("不支持的支付订单类型：" + text1);
    }

    @Override
    public PayableOrder getOrder(String id) {
        Class<? extends PayableOrder> type = toOrderType(id);
        final long id1 = toOrderId(id);
        if (type == MainOrder.class) {
            return mainOrderService.getOrder(id1);
        }
        return promotionRequestRepository.getOne(id1);
    }

    @Override
    @EventListener(OrderPaySuccess.class)
    public void paySuccess(OrderPaySuccess event) {
        log.info("处理付款成功事件");
        if (event.getPayableOrder() instanceof MainOrder) {
            MainOrder mainOrder = (MainOrder) event.getPayableOrder();
            if (mainOrder.isPay())
                throw new IllegalStateException("订单已支付");
            mainOrder.setPayTime(LocalDateTime.now());
            mainOrder.setOrderStatus(OrderStatus.forDeliver);
            mainOrder.setPayOrder(event.getPayOrder());
        } else if (event.getPayableOrder() instanceof PromotionRequest) {
            PromotionRequest request = (PromotionRequest) event.getPayableOrder();
            request.setPaymentStatus(PaymentStatus.payed);
            request.setPayTime(LocalDateTime.now());
            promotionRequestService.submitRequest(request);
        } else
            throw new IllegalStateException("暂时不支持：" + event.getPayableOrder());
//        mainOrderRepository.save(mainOrder);
    }

    @Override
    @EventListener(OrderPayCancellation.class)
    public void payCancel(OrderPayCancellation event) {
        log.info(event.getPayableOrder() + "放弃了支付");
        payOrderRepository.delete(event.getPayOrder());
    }
}

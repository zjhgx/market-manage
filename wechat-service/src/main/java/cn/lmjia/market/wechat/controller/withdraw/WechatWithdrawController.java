package cn.lmjia.market.wechat.controller.withdraw;


import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.entity.MainOrder;
import cn.lmjia.market.core.entity.support.Address;
import cn.lmjia.market.core.entity.withdraw.Invoice;
import cn.lmjia.market.core.entity.withdraw.Withdraw;
import cn.lmjia.market.core.model.ApiResult;
import cn.lmjia.market.core.service.MainOrderService;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.core.service.WechatWithdrawService;
import com.huotu.verification.service.VerificationCodeService;
import me.jiangcai.payment.exception.SystemMaintainException;
import me.jiangcai.wx.OpenId;
import me.jiangcai.wx.model.Gender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import java.io.IOException;
import java.math.BigDecimal;

@Controller
public class WechatWithdrawController{

    private static final Log log = LogFactory.getLog(WechatWithdrawController.class);

    @Autowired
    private WechatWithdrawService wechatWithdrawService;
    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private ReadService readService;

    /**
     * @return 我要提现页面
     */
    @GetMapping("/wechatWithdrawPage")
    public String index(@AuthenticationPrincipal Login login, Model model) {
        return "wechat@withdraw.html";
    }

    /**
     * @return 提现申请提交后，返回验证码验证页面
     */
    @PostMapping("/wechatWithdraw")
    public String withdrawNew(@OpenId String openId, HttpServletRequest request, String payee, String account, String bank, String mobile, String withdrawMoney,
                              String invoice,String logisticsNumber,String logisticsCompany,@AuthenticationPrincipal Login login, Model model)
            throws SystemMaintainException ,IOException {
        if(readService.currentBalance(login).getAmount().compareTo(BigDecimal.valueOf(Double.valueOf(withdrawMoney)))<0){
            log.info("用户可提现余额不足");
            return "wechat@withdraw.html";
        }
        Withdraw withdraw1 = wechatWithdrawService.withdrawNew(payee, account, bank, mobile, withdrawMoney, invoice,logisticsNumber, logisticsCompany);
        verificationCodeService.sendCode(mobile,wechatWithdrawService.withdrawVerificationType());
        return "wechat@withdrawVerify.html";
    }

    @PostMapping("/misc/sendWithdrawCode")
    @ResponseBody
    public ApiResult sendWithdrawCode(String mobile) throws IOException {
        verificationCodeService.sendCode(mobile, wechatWithdrawService.withdrawVerificationType());
        return ApiResult.withOk();
    }

    /**
     * @return 手机验证码验证
     */
    @PostMapping("/withdrawVerify")
    public String withdrawVerify(String mobile,String authCode){
        wechatWithdrawService.checkWithdrawCode(mobile,authCode);
        return "wechat@withdrawSuccess.html";
    }
}

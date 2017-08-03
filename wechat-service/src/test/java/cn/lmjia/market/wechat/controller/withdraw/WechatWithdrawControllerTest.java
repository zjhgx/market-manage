package cn.lmjia.market.wechat.controller.withdraw;

import cn.lmjia.market.core.config.other.SecurityConfig;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.service.ReadService;
import cn.lmjia.market.wechat.WechatTestBase;
import cn.lmjia.market.wechat.page.PaySuccessPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = SecurityConfig.class)
public class WechatWithdrawControllerTest extends WechatTestBase {

    private static final Log log = LogFactory.getLog(WechatWithdrawControllerTest.class);

    @Autowired
    private ReadService readService;
    @Test
    public void go() throws Exception{
        // 测试就是校验我们的工作成功
        // 就提现这个功能而言 我们要做的测试很简单
        // 1，新用户
        // 尝试提现 会看到可以可提现金额为0
        // 强行输入提现 比如 1元 会看到错误信息
        Login user = createNewUserByShare();
        bindDeveloperWechat(user);
        updateAllRunWith(user);

        makeSuccessOrder(user);
//        System.err.println(readService.currentBalance(user));
        driver.get("http://localhost/wechatWithdrawPage");
        assertThat(driver.getTitle())
                .isEqualToIgnoringCase("我要提现");


        String payee = "oneal";
        String account = "6217001480003532428";
        String bank = "建设银行";
        String mobile = "15267286525";

        //新用户可提现佣金余额为0，强行提现
        String withdrawUri = mockMvc.perform(wechatPost("/wechatWithdraw")
                        .param("payee",payee)
                        .param("account", account)
                        .param("bank", bank)
                        .param("mobile", mobile)
                        .param("withdrawMoney", "500.00")
                        .param("invoice", "1")
        )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getHeader("Location");

        driver.get("http://localhost" + withdrawUri);
//        System.err.println(readService.currentBalance(user));
//        //
        // 2，新用户
        // 成功下了一笔订单 获得佣金X
        // 尝试提现 会看到可提现金额为X
        // 强行输入 X+1 会看到错误信息
        // 调整为输入Y (Y<X)
        // 此时验证手机号码
        // 成功验证
        // 会看到可提现金额为X-Y
        // Px---以管理员身份
        // 此时会看到该提现申请
        // 点击拒绝
        // -- 回到用户身份
        // 会看到可提现金额回到X
        //
        // 重复流程2直置Px
        // 此时会看到该提现申请
        // 点击同意
        // -- 回到用户身份
        // 会看到可提现金额回到X-Y
        // 同时看到已提现金额为Y
    }


    @Test
    public void withdrawVerify() throws Exception {
        Login user = createNewUserByShare();
        bindDeveloperWechat(user);
        updateAllRunWith(user);

        String withdrawUri = mockMvc.perform(wechatPost("/misc/sendWithdrawCode")
                        .param("mobile", "15267286525")
        )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getHeader("Location");

        driver.get("http://localhost" + withdrawUri);

    }


}

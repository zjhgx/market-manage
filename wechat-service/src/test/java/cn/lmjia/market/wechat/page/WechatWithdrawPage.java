package cn.lmjia.market.wechat.page;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * 提现页面
 * withdraw.html
 */
public class WechatWithdrawPage extends AbstractWechatPage{

    private WebElement mobile;
    private WebElement authCode;
    private WebElement name;
    @FindBy(css = "button[type=submit]")
    private WebElement submit;
    @FindBy(id = "J_authCode")
    private WebElement sendButton;

    public WechatWithdrawPage(WebDriver webDriver) {
        super(webDriver);
    }

    @Override
    public void validatePage() {
        assertTitle("提现页面");
    }

    public void sendAuthCode(String mobile) {
        this.mobile.clear();
        this.mobile.sendKeys(mobile);
        sendButton.click();
    }

    public void submitSuccessAs(String name) {
        authCode.clear();
        authCode.sendKeys("6456");
        this.name.clear();
        this.name.sendKeys(name);
        submit.click();
    }

    public void submitWithoutInvoice(String payee, String account, String bank, String mobile, String amount) {
        WebElement nameElement = webDriver.findElement(By.name("payee"));
        nameElement.clear();
        nameElement.findElement(By.name("account"));
        nameElement.findElement(By.name("bank"));
        nameElement.findElement(By.name("mobile"));
        nameElement.findElement(By.name("amount"));

        // 点击提交按钮
        nameElement.submit();

    }
}

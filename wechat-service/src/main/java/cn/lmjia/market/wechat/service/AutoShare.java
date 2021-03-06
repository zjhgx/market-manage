package cn.lmjia.market.wechat.service;

import cn.lmjia.market.wechat.entity.LimitQRCode;
import cn.lmjia.market.wechat.repository.LimitQRCodeRepository;
import me.jiangcai.wx.classic.TempSceneReply;
import me.jiangcai.wx.message.Message;
import me.jiangcai.wx.model.PublicAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author CJ
 */
@Component
public class AutoShare extends TempSceneReply {

    @Autowired
    private LimitQRCodeRepository limitQRCodeRepository;
    @Autowired
    private WechatService wechatService;

    @Override
    public void happen(PublicAccount account, Message message, int sceneId) {
        LimitQRCode code = limitQRCodeRepository.getOne(sceneId);
        code.setLastUseTime(LocalDateTime.now());
        limitQRCodeRepository.save(code);

        wechatService.shareTo(code.getLogin().getId(), message.getFrom());
    }
}

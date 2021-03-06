package cn.lmjia.market.core.controller;

import cn.lmjia.market.core.entity.ContactWay;
import cn.lmjia.market.core.entity.Login;
import cn.lmjia.market.core.row.FieldDefinition;
import cn.lmjia.market.core.row.RowCustom;
import cn.lmjia.market.core.row.RowDefinition;
import cn.lmjia.market.core.row.field.Fields;
import cn.lmjia.market.core.row.supplier.Select2Dramatizer;
import cn.lmjia.market.core.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.Arrays;
import java.util.List;

/**
 * 身份数据相关控制器
 *
 * @author CJ
 */
@Controller
public class LoginDataController {

    @Autowired
    private LoginService loginService;

    /**
     * 公开可用的手机号码可用性校验
     *
     * @param mobile 确认的手机号码
     * @return 可用性
     */
    @GetMapping("/loginData/mobileValidation")
    @ResponseBody
    public boolean mobileValidation(@RequestParam String mobile) {
        return loginService.mobileValidation(mobile);
    }

    @PreAuthorize("!isAnonymous()")
    @GetMapping("/loginData/select2")
    @RowCustom(dramatizer = Select2Dramatizer.class, distinct = true)
    public RowDefinition<Login> searchLoginSelect2(String search) {
        return searchLogin(search);
    }

    /**
     * 查询所有用户
     *
     * @param search 可能是名字或者电话号码
     * @return 字段定义
     */
    private RowDefinition<Login> searchLogin(String search) {
        return new RowDefinition<Login>() {
            @Override
            public Class<Login> entityClass() {
                return Login.class;
            }

            @Override
            public List<FieldDefinition<Login>> fields() {
                return Arrays.asList(Fields.asBasic("id")
                        , Fields.asFunction("name", root -> root.get("contactWay").get("name"))
                        , Fields.asFunction("mobile", root -> root.get("contactWay").get("mobile"))
                );
            }

            @Override
            public Specification<Login> specification() {
                if (StringUtils.isEmpty(search))
                    return (root, query, cb) -> {
                        // 必须得有 所以right
                        Join<Login, ContactWay> contactWayJoin = root.join("contactWay", JoinType.INNER);
                        return cb.isNotNull(contactWayJoin);
                    };
                String jpaSearch = "%" + search + "%";
                return (root, query, cb) -> {
                    // 必须得有 所以right
                    Join<Login, ContactWay> contactWayJoin = root.join("contactWay", JoinType.INNER);
                    return cb.or(
                            cb.like(contactWayJoin.get("mobile"), jpaSearch)
                            , cb.like(contactWayJoin.get("name"), jpaSearch)
                    );
                };
            }
        };
    }


}

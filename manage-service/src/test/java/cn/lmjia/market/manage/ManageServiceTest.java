package cn.lmjia.market.manage;

import cn.lmjia.market.core.config.other.SecurityConfig;
import cn.lmjia.market.dealer.DealerServiceTest;
import cn.lmjia.market.manage.config.ManageConfig;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author CJ
 */
@ContextConfiguration(classes = {ManageConfig.class, SecurityConfig.class})
public abstract class ManageServiceTest extends DealerServiceTest {
}

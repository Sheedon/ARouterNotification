package org.sheedon.arouter.core;

import org.sheedon.arouter.model.ITrigger;

import java.util.HashMap;
import java.util.Map;

/**
 * Storage of route meta and other data.
 *
 * @author zhilong <a href="mailto:zhilong.lzl@alibaba-inc.com">Contact me.</a>
 * @version 1.0
 * @since 2017/2/23 下午1:39
 */
class Warehouse {
    static Map<String, ITrigger> notificationMap = new HashMap<>();
}

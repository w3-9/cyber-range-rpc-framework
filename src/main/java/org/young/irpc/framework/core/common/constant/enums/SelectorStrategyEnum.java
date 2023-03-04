package org.young.irpc.framework.core.common.constant.enums;

import org.young.irpc.framework.core.common.cache.CommonClientCache;
import org.young.irpc.framework.core.router.IRouter;
import org.young.irpc.framework.core.router.impl.RandomRouterImpl;
import org.young.irpc.framework.core.router.impl.RotateRouteImpl;

public enum SelectorStrategyEnum {
    random("random"),
    rotate("rotate");

    private final String text;

    SelectorStrategyEnum(final String text){
        this.text = text;
    }


    @Override
    public String toString() {
        return text;
    }

    public static IRouter  getRouter(String value) {
        switch (SelectorStrategyEnum.valueOf(value)) {
            case random:
                return new RandomRouterImpl();
            case rotate:
                return new RotateRouteImpl();
            default:
                return new RandomRouterImpl();
        }
    }

}

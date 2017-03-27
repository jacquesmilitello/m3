package io.m3.sql.factory;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public class FactoryLoader {

    static {
        ServiceLoader<FactoryFacade> facade = ServiceLoader.load(FactoryFacade.class);

        Iterator<FactoryFacade> it = facade.iterator();
        while(it.hasNext()) {

        }

    }
}
package io.m3.sql.apt.analyser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

import io.m3.sql.annotation.GlobalConfiguration;
import io.m3.sql.apt.log.Logger;
import io.m3.sql.apt.log.LoggerFactory;
import io.m3.sql.apt.model.GlobalConfigurationDescriptor;
import io.m3.sql.apt.model.PojoDescriptor;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class GlobalConfigurationAnalyser implements Function<Element, GlobalConfigurationDescriptor> {

	private final List<PojoDescriptor> descriptors;
	private final Logger logger;

	public GlobalConfigurationAnalyser(List<PojoDescriptor> descriptors) {
		this.descriptors = new ArrayList<>(descriptors);
		this.logger = LoggerFactory.getInstance().getLogger(GlobalConfigurationAnalyser.class);
	}

	@Override
	public GlobalConfigurationDescriptor apply(Element element) {

		if (ElementKind.INTERFACE != element.getKind()) {
			logger.error("element [" + element + "] should be an interface");
			return null;
		}

		logger.info("Analyse Global Configuration " + element);

		GlobalConfiguration gc = element.getAnnotation(GlobalConfiguration.class);

		List<PojoDescriptor> found = new ArrayList<>();

		for (String pack : gc.flywayConfiguration().packages()) {
			Iterator<PojoDescriptor> it = descriptors.iterator();
			while (it.hasNext()) {
				PojoDescriptor desc = it.next();
				if (desc.element().toString().startsWith(pack)) {
					found.add(desc);
					it.remove();
				}
			}
		}

		descriptors.forEach(desc -> {
			logger.error("No GlogalConfiguration found for " + desc.element().toString());
		});

		return new GlobalConfigurationDescriptor(element, found, gc);
	}

}

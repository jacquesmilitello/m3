package io.m3.sql.apt.model;

import java.util.List;

import javax.lang.model.element.Element;

import io.m3.sql.annotation.GlobalConfiguration;

public final class GlobalConfigurationDescriptor {

	private final Element element;
	private final List<PojoDescriptor> descriptors;
	private final GlobalConfiguration globalConfiguration;
	
	public GlobalConfigurationDescriptor(Element element, List<PojoDescriptor> descriptors, GlobalConfiguration gc) {
		this.element = element;
		this.descriptors = descriptors;
		this.globalConfiguration = gc;
	}

	public Element getElement() {
		return element;
	}

	public List<PojoDescriptor> getDescriptors() {
		return descriptors;
	}

	public GlobalConfiguration getGlobalConfiguration() {
		return globalConfiguration;
	}
	
	
	
}

package io.m3.sql.apt.model;

import io.m3.sql.annotation.BusinessKey;
import io.m3.sql.annotation.JoinTable;
import io.m3.sql.annotation.Table;

import javax.lang.model.element.Element;

import java.util.List;
import java.util.stream.Collectors;

public final class PojoDescriptor {

    private final Element element;
    private final List<PojoPropertyDescriptor> propertyDescriptors;
    private final List<PojoPropertyDescriptor> idDescriptors;

    public PojoDescriptor(Element element, List<PojoPropertyDescriptor> idDescriptors, List<PojoPropertyDescriptor> propertyDescriptors) {
        this.element = element;
        this.idDescriptors = idDescriptors;
        this.propertyDescriptors = propertyDescriptors;
    }
    
    public String getPackage() {
    	String fcqn = fullyQualidiedClassName();
    	return fcqn.substring(0, fcqn.lastIndexOf('.'));
    }

    public String fullyQualidiedClassName() {
        return this.element.asType().toString();
    }

    public String simpleName() {
        String fqcn =  this.element.asType().toString();
        return fqcn.substring(fqcn.lastIndexOf('.') + 1);
    }

    public Element element() {
        return this.element;
    }

    public List<PojoPropertyDescriptor> properties() {
        return this.propertyDescriptors;
    }

    public List<PojoPropertyDescriptor> ids() {
        return this.idDescriptors;
    }

    public List<PojoPropertyDescriptor> businessKeys() {
        return propertyDescriptors.stream()
                .filter(ppd -> ppd.getter().getAnnotation(BusinessKey.class) != null)
                .collect(Collectors.toList());
    }
    
    public Table getTable() {
    	return this.element.getAnnotation(Table.class);
    }
    
    public JoinTable getJoinTable() {
    	return this.element.getAnnotation(JoinTable.class);
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PojoDescriptor [element=");
		builder.append(element);
		builder.append("]");
		this.idDescriptors.forEach(ppd -> {
			builder.append("\n\t\tid [").append(ppd).append("]");
		});

		this.propertyDescriptors.forEach(ppd -> {
			builder.append("\n\t\tproperty [").append(ppd).append("]");
		});
		return builder.toString();
	}
    
    
}

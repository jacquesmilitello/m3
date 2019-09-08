package io.m3.sql.apt;

import javax.annotation.processing.ProcessingEnvironment;

import io.m3.sql.apt.model.PojoDescriptor;

import java.util.List;
import java.util.Map;

public interface Generator {

    void generate(ProcessingEnvironment env, List<PojoDescriptor> descriptors, Map<String, Object> properties);

}
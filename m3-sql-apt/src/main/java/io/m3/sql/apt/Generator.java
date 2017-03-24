package io.m3.sql.apt;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.List;

public interface Generator {

    void generate(ProcessingEnvironment env, List<PojoDescriptor> descriptors);

}
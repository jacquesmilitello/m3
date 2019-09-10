package io.m3.sql.apt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import io.m3.sql.annotation.GlobalConfiguration;
import io.m3.sql.annotation.JoinTable;
import io.m3.sql.annotation.Table;
import io.m3.sql.apt.analyser.GlobalConfigurationAnalyser;
import io.m3.sql.apt.analyser.JoinTableAnalyser;
import io.m3.sql.apt.analyser.SqlInterfaceAnalyser;
import io.m3.sql.apt.flyway.FlywayGenerator;
import io.m3.sql.apt.log.Logger;
import io.m3.sql.apt.log.LoggerFactory;
import io.m3.sql.apt.model.GlobalConfigurationDescriptor;
import io.m3.sql.apt.model.PojoDescriptor;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"io.m3.sql.annotation.GlobalConfiguration","io.m3.sql.annotation.Table"})
public final class SqlProcessor extends AbstractProcessor {

    private ProcessingEnvironment processingEnvironment;

    private boolean firstTime = false;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.processingEnvironment = processingEnv;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (firstTime) {
            this.processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, "SqlProcessor done");
            return false;
        }

    	LoggerFactory.getInstance().init(processingEnv);
    	
    	Logger logger = LoggerFactory.getInstance().getLogger(SqlProcessor.class);
    	logger.info("SqlProcessor start");

        doProcess(roundEnv);

    	logger.info("SqlProcessor end");
        this.firstTime = true;
        
        LoggerFactory.getInstance().close();
        
        return true;
    }

    private void doProcess(RoundEnvironment roundEnvironment) {

    	Logger logger = LoggerFactory.getInstance().getLogger(SqlProcessor.class);
    	
        List<PojoDescriptor> descriptors = roundEnvironment.getElementsAnnotatedWith(Table.class)
                .stream()
                .map(new SqlInterfaceAnalyser())
                .collect(Collectors.toList());
        
        List<PojoDescriptor> joinTableDescriptors = roundEnvironment.getElementsAnnotatedWith(JoinTable.class)
                .stream()
                .map(new JoinTableAnalyser(descriptors))
                .collect(Collectors.toList());

        logger.info("Result Analysis -----------------------------------------------------------------------------------------");
        logger.info("---------------------------------------------------------------------------------------------------------");
        logger.info("Number of Pojo(s) found : " + descriptors.size());
        descriptors.forEach(pojoDesc -> {
        	logger.info("\t->" + pojoDesc);
        });
        logger.info("Number of Joint Table Pojo(s) found : " + joinTableDescriptors.size());
        descriptors.forEach(pojoDesc -> {
        	logger.info("\t->" + pojoDesc);
        });
        logger.info("---------------------------------------------------------------------------------------------------------");
        
        
        List<PojoDescriptor> all = new ArrayList<>();
        all.addAll(descriptors);
        all.addAll(joinTableDescriptors);
        
        
        Map<String, Object> properties = new HashMap<>();
        new PojoImplementationGenerator().generate(this.processingEnv, all, properties);
        new PojoFactoryGenerator().generate(this.processingEnv, all, properties);
        new PojoDescriptorGenerator().generate(this.processingEnv, all, properties);
        new RepositoryGenerator().generate(this.processingEnv, all, properties);
        new MapperGenerator().generate(this.processingEnv, all, properties);
        new PojoMapperFactoryGenerator().generate(this.processingEnv, all, properties);
        new ModuleGenerator().generate(this.processingEnv, all, properties);
        
        
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(GlobalConfiguration.class);

        logger.info("Global configuration found : " + elements);

        List<GlobalConfigurationDescriptor> configs = roundEnvironment.getElementsAnnotatedWith(GlobalConfiguration.class)
            	.stream()
            	.map(new GlobalConfigurationAnalyser(all))
            	.collect(Collectors.toList());
        
        new FlywayGenerator().generate(this.processingEnv, configs);

    }

}
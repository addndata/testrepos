package com.project.admin.dao;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailContentBuilderService {
 
    private TemplateEngine templateEngine;
 
    @Autowired
    public MailContentBuilderService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }
 
    public String build(String template_name,HashMap<String, String> nameValMap) {
        Context context = new Context();
        Iterator it = nameValMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            context.setVariable(pair.getKey().toString(), pair.getValue().toString());
            it.remove(); // avoids a ConcurrentModificationException
        }
        //context.setVariable("message", message);
        return templateEngine.process(template_name, context);
    }
 
}
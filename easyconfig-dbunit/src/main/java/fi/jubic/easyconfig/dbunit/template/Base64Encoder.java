package fi.jubic.easyconfig.dbunit.template;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class Base64Encoder implements TemplateMethodModelEx {
    @Override
    public Object exec(List list) throws TemplateModelException {
        if (list.size() != 1) {
            throw new TemplateModelException("Invalid number of arguments");
        }

        try {
            return Base64.getEncoder().encodeToString(
                    ((TemplateScalarModel) list.get(0))
                            .getAsString()
                            .getBytes(StandardCharsets.UTF_8)
            );
        }
        catch (ClassCastException ignore) {
            throw new TemplateModelException("Invalid argument type.");
        }
    }
}

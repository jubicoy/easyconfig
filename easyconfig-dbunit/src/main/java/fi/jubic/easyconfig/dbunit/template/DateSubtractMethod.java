package fi.jubic.easyconfig.dbunit.template;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;

import java.util.Date;
import java.util.List;

class DateSubtractMethod implements TemplateMethodModelEx {
    private final Date now;

    DateSubtractMethod(Date now) {
        this.now = now;
    }

    @Override
    public Object exec(List list) throws TemplateModelException {
        if (list.size() != 2) {
            throw new TemplateModelException("Invalid number of arguments");
        }

        return DateMethodUtil.toTimestamp(
                DateMethodUtil.subtract(
                        now,
                        DateMethodUtil.<TemplateNumberModel>safeGet(list, 0)
                                .getAsNumber()
                                .intValue(),
                        DateMethodUtil.<TemplateScalarModel>safeGet(list, 1)
                                .getAsString()
                )
        );
    }
}

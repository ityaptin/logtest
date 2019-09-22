package com.example.logtest;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.micrometer.core.instrument.util.StringUtils;

import javax.validation.constraints.NotEmpty;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MaskingPatternLayout extends PatternLayout {

    private List<String> patternsProperties;
    private Pattern pattern;

    public List<String> getPatternsProperties() {
        return patternsProperties;
    }

    public void setPatternsProperty(String patternsPropertyString) {
        if (StringUtils.isBlank(patternsPropertyString)) {
            patternsProperties = Collections.emptyList();
            pattern = null;
            return;
        }
        List<String> properties = Arrays.stream(patternsPropertyString.split(","))
                                        .filter(it -> !StringUtils.isBlank(it))
                                        .map(String::trim)
                                        .collect(Collectors.toList());
        this.pattern = Pattern.compile(toPatternString(properties), Pattern.MULTILINE);
    }

    private String toPatternString(@NotEmpty List<String> patternsProperty) {
        return "(\"?" +
                String.join("|", patternsProperty) +
                "\"?)" +
                "\\s*:\\s*" +
                "\"([\\w\\W]+)\",";
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        final StringBuilder message = new StringBuilder(super.doLayout(event));

        if (pattern != null) {
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {

                int group = 2;
                while (group <= matcher.groupCount()) {
                    if (matcher.group(group) != null) {
                        for (int i = matcher.start(group); i < matcher.end(group); i++) {
                            message.setCharAt(i, '*');
                        }
                    }
                    group++;
                }
            }
        }
        return message.toString();
    }

}

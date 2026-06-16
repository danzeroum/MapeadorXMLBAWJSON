package br.com.danzeroum.processveritas.config.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.springframework.context.ApplicationContext;

import java.time.Instant;

public class SseBroadcastAppender extends AppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent event) {
        ApplicationContext ctx = ApplicationContextHolder.getContext();
        if (ctx == null) return;

        try {
            LogBroadcaster broadcaster = ctx.getBean(LogBroadcaster.class);
            if (broadcaster.activeCount() == 0) return;

            String msg = String.format("[%s] %s %s - %s",
                    event.getLevel().levelStr,
                    Instant.ofEpochMilli(event.getTimeStamp()),
                    shortenLogger(event.getLoggerName()),
                    event.getFormattedMessage());
            broadcaster.broadcast(msg);
        } catch (Exception ignored) {
        }
    }

    private static String shortenLogger(String loggerName) {
        if (loggerName == null) return "";
        int lastDot = loggerName.lastIndexOf('.');
        return lastDot >= 0 ? loggerName.substring(lastDot + 1) : loggerName;
    }
}

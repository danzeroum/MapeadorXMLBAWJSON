package br.com.danzeroum.processveritas.web.controller;

import br.com.danzeroum.processveritas.config.logging.LogBroadcaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/system")
public class SystemLogController {

    @Autowired
    private LogBroadcaster logBroadcaster;

    @GetMapping(path = "/logs", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamLogs() {
        return logBroadcaster.register();
    }
}

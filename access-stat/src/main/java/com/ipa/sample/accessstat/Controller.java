package com.ipa.sample.accessstat;

import com.ipa.sample.common.AccessStat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RestController
public class Controller {

    private static final AtomicLong id = new AtomicLong(0);

    private final AccessStatRepository accessStatRepository;

    public Controller(AccessStatRepository accessStatRepository) {
        this.accessStatRepository = accessStatRepository;
    }

    @PostMapping("/append")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public AccessStat post(@RequestBody AccessStat stat) {

        AccessStatEntity entity = new AccessStatEntity();

        entity.setId(id.incrementAndGet());
        entity.setAccessDate(stat.getAccessDate());
        entity.setTimeCostInMilliSeconds(stat.getTimeCostInMilliSeconds());
        entity.setWeather(stat.getWeather());

        accessStatRepository.save(entity);

        return stat;
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<AccessStatEntity> list() {
        return accessStatRepository.findAll();
    }

    @GetMapping("/count")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String count() {
        return String.valueOf(list().size());
    }
}

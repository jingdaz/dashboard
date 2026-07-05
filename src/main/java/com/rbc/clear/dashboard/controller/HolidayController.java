package com.rbc.clear.dashboard.controller;

import com.rbc.clear.dashboard.dto.UploadHolidaysResult;
import com.rbc.clear.dashboard.model.Holiday;
import com.rbc.clear.dashboard.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/holidays")
public class HolidayController {
    @Autowired
    private HolidayService holidayService;

    public HolidayController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<Holiday>> getAllHolidays(@RequestParam("country") String country) {
        return ResponseEntity.ok(holidayService.getAllHolidays(country));
    }

    @PostMapping("/add")
    public ResponseEntity<Holiday> createHoliday(@RequestBody Holiday holiday) {
        return ResponseEntity.ok(holidayService.createHoliday(holiday));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Holiday> updateHoliday(@PathVariable Long id, @RequestBody Holiday holiday) {
        Holiday result = holidayService.updateHoliday(id, holiday);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping(value="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadHolidaysResult> uploadHolidays(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(holidayService.uploadHolidays(file));
    }
}

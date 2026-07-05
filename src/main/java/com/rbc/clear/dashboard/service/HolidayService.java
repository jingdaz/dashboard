package com.rbc.clear.dashboard.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rbc.clear.dashboard.dto.UploadHolidaysResult;
import com.rbc.clear.dashboard.model.Holiday;
import com.rbc.clear.dashboard.repository.HolidayRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class HolidayService {
    private final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    private final HolidayRepository holidayRepository;

    public HolidayService(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }


    public List<Holiday> getAllHolidays(String country) {
        return holidayRepository.findByCountry(country);
    }

    public Holiday createHoliday(Holiday holiday) {
        if (holiday.getYear() == null) {
            holiday.setYear(holiday.getDate().getYear());
        }
        return holidayRepository.save(holiday);
    }

    public Holiday updateHoliday(Long id, Holiday holiday) {
        Optional<Holiday> found = holidayRepository.findById(id);
        if (found.isEmpty()) {
            return null;
        }
        Holiday existingHoliday = found.get();
        existingHoliday.setCountry(holiday.getCountry());
        existingHoliday.setName(holiday.getName());
        existingHoliday.setDate(holiday.getDate());
        existingHoliday.setYear(holiday.getDate().getYear());
        return holidayRepository.save(existingHoliday);
    }

    public UploadHolidaysResult uploadHolidays(MultipartFile file) {
        if (file.isEmpty()) {
            return new UploadHolidaysResult(0, 0, 0);
        }
        try {
            List<Holiday> uploadedHolidays = objectMapper.readValue(file.getInputStream(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Holiday.class));
            int totalRecords = uploadedHolidays.size();
            AtomicInteger successfulRecords = new AtomicInteger();
            AtomicInteger failedRecords = new AtomicInteger();
            uploadedHolidays.forEach(holiday -> {
                        try {
                            holidayRepository.save(holiday);
                            successfulRecords.getAndIncrement();
                        } catch (Exception e) {
                            failedRecords.getAndIncrement();
                        }
                    }
            );
            return new UploadHolidaysResult(totalRecords, successfulRecords.get(), failedRecords.get());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.rbc.clear.dashboard.service;

import com.rbc.clear.dashboard.dto.UploadHolidaysResult;
import com.rbc.clear.dashboard.model.Holiday;
import com.rbc.clear.dashboard.repository.HolidayRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HolidayServiceTest {

    @Mock
    private HolidayRepository holidayRepository;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private HolidayService holidayService;

    @Test
    void getAllHolidaysReturnsHolidaysForCountry() {
        List<Holiday> holidays = List.of(
                new Holiday("CA", "Canada Day", LocalDate.of(2026, 7, 1)),
                new Holiday("CA", "Thanksgiving", LocalDate.of(2026, 10, 12))
        );
        when(holidayRepository.findByCountry("CA")).thenReturn(holidays);

        List<Holiday> result = holidayService.getAllHolidays("CA");

        assertSame(holidays, result);
        verify(holidayRepository).findByCountry("CA");
    }

    @Test
    void createHolidaySetsYearWhenMissing() {
        Holiday holiday = new Holiday();
        holiday.setCountry("US");
        holiday.setName("Independence Day");
        holiday.setDate(LocalDate.of(2026, 7, 4));
        when(holidayRepository.save(holiday)).thenReturn(holiday);

        Holiday result = holidayService.createHoliday(holiday);

        assertSame(holiday, result);
        assertEquals(2026, holiday.getYear());
        verify(holidayRepository).save(holiday);
    }

    @Test
    void createHolidayKeepsExistingYearWhenPresent() {
        Holiday holiday = new Holiday();
        holiday.setCountry("US");
        holiday.setName("Observed Holiday");
        holiday.setDate(LocalDate.of(2026, 1, 1));
        holiday.setYear(2025);
        when(holidayRepository.save(holiday)).thenReturn(holiday);

        Holiday result = holidayService.createHoliday(holiday);

        assertSame(holiday, result);
        assertEquals(2025, holiday.getYear());
        verify(holidayRepository).save(holiday);
    }

    @Test
    void updateHolidayReturnsNullWhenHolidayDoesNotExist() {
        Holiday update = new Holiday("CA", "Canada Day", LocalDate.of(2026, 7, 1));
        when(holidayRepository.findById(10L)).thenReturn(Optional.empty());

        Holiday result = holidayService.updateHoliday(10L, update);

        assertNull(result);
        verify(holidayRepository).findById(10L);
        verify(holidayRepository, never()).save(any(Holiday.class));
    }

    @Test
    void updateHolidayUpdatesExistingHolidayAndSavesIt() {
        Holiday existing = new Holiday("US", "Old Name", LocalDate.of(2025, 1, 1));
        Holiday update = new Holiday("CA", "Canada Day", LocalDate.of(2026, 7, 1));
        when(holidayRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(holidayRepository.save(existing)).thenReturn(existing);

        Holiday result = holidayService.updateHoliday(5L, update);

        assertSame(existing, result);
        assertEquals("CA", existing.getCountry());
        assertEquals("Canada Day", existing.getName());
        assertEquals(LocalDate.of(2026, 7, 1), existing.getDate());
        assertEquals(2026, existing.getYear());
        verify(holidayRepository).save(existing);
    }

    @Test
    void uploadHolidaysReturnsZeroCountsForEmptyFile() {
        when(file.isEmpty()).thenReturn(true);

        UploadHolidaysResult result = holidayService.uploadHolidays(file);

        assertEquals(0, result.getTotalRecords());
        assertEquals(0, result.getSuccessfulRecords());
        assertEquals(0, result.getFailedRecords());
        verify(holidayRepository, never()).save(any(Holiday.class));
    }

    @Test
    void uploadHolidaysSavesParsedHolidaysAndReportsSuccesses() throws IOException {
        String json = "["
                + "{\"country\":\"CA\",\"name\":\"Canada Day\",\"date\":\"2026-07-01\",\"year\":2026},"
                + "{\"country\":\"US\",\"name\":\"Independence Day\",\"date\":\"2026-07-04\",\"year\":2026}"
                + "]";
        when(file.isEmpty()).thenReturn(false);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
        when(holidayRepository.save(any(Holiday.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UploadHolidaysResult result = holidayService.uploadHolidays(file);

        assertEquals(2, result.getTotalRecords());
        assertEquals(2, result.getSuccessfulRecords());
        assertEquals(0, result.getFailedRecords());

        ArgumentCaptor<Holiday> captor = ArgumentCaptor.forClass(Holiday.class);
        verify(holidayRepository, times(2)).save(captor.capture());
        assertEquals("CA", captor.getAllValues().get(0).getCountry());
        assertEquals(LocalDate.of(2026, 7, 4), captor.getAllValues().get(1).getDate());
    }

    @Test
    void uploadHolidaysReportsFailedRecordsWhenRepositorySaveFails() throws IOException {
        String json = "["
                + "{\"country\":\"CA\",\"name\":\"Canada Day\",\"date\":\"2026-07-01\",\"year\":2026},"
                + "{\"country\":\"US\",\"name\":\"Independence Day\",\"date\":\"2026-07-04\",\"year\":2026}"
                + "]";
        when(file.isEmpty()).thenReturn(false);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
        when(holidayRepository.save(any(Holiday.class)))
                .thenAnswer(invocation -> invocation.getArgument(0))
                .thenThrow(new RuntimeException("duplicate holiday"));

        UploadHolidaysResult result = holidayService.uploadHolidays(file);

        assertEquals(2, result.getTotalRecords());
        assertEquals(1, result.getSuccessfulRecords());
        assertEquals(1, result.getFailedRecords());
        verify(holidayRepository, times(2)).save(any(Holiday.class));
    }

    @Test
    void uploadHolidaysThrowsRuntimeExceptionWhenFileCannotBeRead() throws IOException {
        when(file.isEmpty()).thenReturn(false);
        when(file.getInputStream()).thenThrow(new IOException("cannot read file"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> holidayService.uploadHolidays(file));

        assertEquals(IOException.class, exception.getCause().getClass());
        verify(holidayRepository, never()).save(any(Holiday.class));
    }
}

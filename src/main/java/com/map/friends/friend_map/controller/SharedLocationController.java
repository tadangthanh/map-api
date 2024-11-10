package com.map.friends.friend_map.controller;

import com.map.friends.friend_map.dto.SharedLocationDto;
import com.map.friends.friend_map.dto.request.SharedLocationRequest;
import com.map.friends.friend_map.dto.response.ResponseData;
import com.map.friends.friend_map.service.ISharedLocationService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/shared-locations")
@RestController
@Validated
public class SharedLocationController {
    private final ISharedLocationService sharedLocationService;

    @PostMapping
    public ResponseData<?> shareLocation(@Validated @RequestBody SharedLocationRequest sharedLocationRequest) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Location shared successfully", sharedLocationService.share(sharedLocationRequest));
    }

    @GetMapping("/with-me")
    public ResponseData<?> getSharedLocationsWithMe(@Min(0) @RequestParam(defaultValue = "0") int page, @Min(1) @RequestParam(defaultValue = "5") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Shared locations retrieved successfully", sharedLocationService.fetchLocationsSharedWithMe(page, size));
    }

    @GetMapping("/by-me")
    public ResponseData<?> getSharedLocationsByMe(@Min(0) @RequestParam(defaultValue = "0") int page, @Min(1) @RequestParam(defaultValue = "5") int size) {
        return new ResponseData<>(HttpStatus.OK.value(), "Shared locations retrieved successfully", sharedLocationService.fetchSharedLocationsByMe(page, size));
    }

    @DeleteMapping("/{id}")
    public ResponseData<?> deleteSharedLocation(@PathVariable Long id) {
        sharedLocationService.delete(id);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Shared location deleted successfully", null);
    }
}

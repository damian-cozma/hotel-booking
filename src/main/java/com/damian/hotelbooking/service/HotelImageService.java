package com.damian.hotelbooking.service;

import com.damian.hotelbooking.entity.Hotel;
import com.damian.hotelbooking.entity.HotelImage;
import com.damian.hotelbooking.repository.HotelImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class HotelImageService {

    private final HotelImageRepository hotelImageRepository;
    private final String uploadDir = "src/main/resources/static/images/";

    public HotelImageService(HotelImageRepository hotelImageRepository) {
        this.hotelImageRepository = hotelImageRepository;
    }

    public void saveImages(List<MultipartFile> files, Hotel hotel) {
        if (files == null) return;

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            try {
                Files.write(filePath, file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            HotelImage img = new HotelImage();
            img.setFileName(fileName);
            img.setUrl("/images/" + fileName);
            img.setHotel(hotel);

            hotelImageRepository.save(img);
        }
    }

    public List<HotelImage> getImagesByHotel(Long hotelId) {
        return hotelImageRepository.findAllByHotelId(hotelId);
    }
}

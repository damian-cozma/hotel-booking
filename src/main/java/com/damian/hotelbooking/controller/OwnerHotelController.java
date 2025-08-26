package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.dto.RoomDto;
import com.damian.hotelbooking.entity.RoomType;
import com.damian.hotelbooking.repository.HotelRepository;
import com.damian.hotelbooking.service.HotelService;
import com.damian.hotelbooking.service.RoomService;
import com.damian.hotelbooking.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/owner/hotels")
public class OwnerHotelController {

    private final HotelService hotelService;
    private final HotelRepository hotelRepository;
    private final RoomService roomService;

    public OwnerHotelController(HotelService hotelService, HotelRepository hotelRepository, RoomService roomService) {
        this.hotelService = hotelService;
        this.hotelRepository = hotelRepository;
        this.roomService = roomService;
    }

    @GetMapping("/new")
    public String showCreateHotelForm(Model model) {

        model.addAttribute("hotelDto", new HotelDto());
        return "owner/hotel-form";

    }

    @PostMapping("/new")
    public String createHotel(@Valid @ModelAttribute("hotelDto") HotelDto hotelDto,
                           BindingResult bindingResult,
                           Principal principal) {

        if (bindingResult.hasErrors()) {
            System.out.println("Errors found: " + bindingResult.getAllErrors());
            return "owner/hotel-form";
        }

        hotelService.saveHotel(hotelDto, bindingResult, principal);

        return "redirect:/";
    }

    @GetMapping("/list")
    public String listHotels(Model model, Principal principal) {

        model.addAttribute("hotels", hotelService.findAllByOwnerId(principal));
        return "owner/list";

    }

    @GetMapping("/edit/{hotelId}")
    public String showUpdateHotelForm(@PathVariable Long hotelId,
                                      Model model,
                                      Principal principal) {

        hotelService.checkOwnership(hotelId, principal);

        model.addAttribute("hotelDto", hotelService.findById(hotelId));
        return "owner/hotel-form";

    }

    @PutMapping("/{hotelId}/edit")
    public String updateHotel(@PathVariable Long hotelId,
                              @Valid @ModelAttribute("hotelDto") HotelDto hotelDto,
                              BindingResult bindingResult,
                              Principal principal) {

        if (bindingResult.hasErrors()) {
            return "owner/hotel-form";
        }

        hotelService.saveHotel(hotelDto, bindingResult, principal);
        return "redirect:/owner/hotels/list";

    }

    @DeleteMapping("/{hotelId}")
    public String deleteHotel(@PathVariable("hotelId") Long userId) {

        hotelRepository.deleteById(userId);
        return "redirect:/owner/hotels/list";

    }

    @GetMapping("/{hotelId}/rooms")
    public String listRooms(@PathVariable("hotelId") Long hotelId, Model model) {

        HotelDto hotel = hotelService.findById(hotelId);
        model.addAttribute("hotel", hotel);
        model.addAttribute("rooms", hotel.getRooms());
        return "owner/rooms/list";

    }

    @GetMapping("/{hotelId}/rooms/add")
    public String showAddRoomForm(@PathVariable("hotelId") Long hotelId, Model model, Principal principal) {

        hotelService.checkOwnership(hotelId, principal);

        RoomDto roomDto = new RoomDto();
        roomDto.setHotelId(hotelId);

        model.addAttribute("room", roomDto);
        model.addAttribute("roomTypes", RoomType.values());

        return "owner/rooms/add";
    }


    @PostMapping("/{hotelId}/rooms/add")
    public String addRoom(@PathVariable("hotelId") Long hotelId,
                          @Valid @ModelAttribute("room") RoomDto roomDto,
                          BindingResult bindingResult,
                          Principal principal) {

        if (bindingResult.hasErrors()) {
            return "owner/rooms/add";
        }

        roomService.addRoom(hotelId, roomDto, bindingResult, principal);

        return "redirect:/owner/hotels/" + hotelId + "/rooms";
    }

}

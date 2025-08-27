package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getAllUsers(Model model) {

        model.addAttribute("users", userService.findAll());
        return "admin/list-users";

    }

    @GetMapping("/new")
    public String showCreateUserForm(Model model) {

        model.addAttribute("user", new User());
        return "admin/user-form";

    }

    @PostMapping
    public String createUser(@ModelAttribute("user") User user) {

        userService.save(user);
        return "redirect:/users";

    }

    @GetMapping("/{userId}/edit")
    public String showUpdateUserForm(@PathVariable("userId") Long userId, Model model) {

        model.addAttribute("user", userService.findById(userId));
        return "admin/user-form";

    }

    @PutMapping("/{userId}")
    public String updateUser(@PathVariable("userId") Long userId, @ModelAttribute("user") User user) {

        user.setId(userId);
        userService.save(user);
        return "redirect:/users";

    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable("userId") Long userId) {

        userService.deleteById(userId);
        return "redirect:/users";

    }
}

package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.entity.Booking;
import com.damian.hotelbooking.exception.BookingNotFoundException;
import com.damian.hotelbooking.repository.BookingRepository;
import com.damian.hotelbooking.service.BookingService;
import com.damian.hotelbooking.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
        import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final UserService userService;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    public BookingController(UserService userService,
                             BookingService bookingService,
                             BookingRepository bookingRepository) {
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
    }

    @GetMapping
    public String listBookings(Model model, Principal principal) {

        Long id = userService.findIdByUsername(principal.getName());
        List<Booking> bookings = bookingRepository.findByUserId(id);

        model.addAttribute("bookings", bookings);
        return "common/hotels/bookings";

    }

    @DeleteMapping("/{bookingId}/cancel")
    public String cancelBooking(@PathVariable Long bookingId,
                                Principal principal) {

        bookingService.cancelBooking(bookingId);

        return "redirect:/bookings";
    }

    @GetMapping("/{bookingId}")
    public String showBookingDetails(@PathVariable Long bookingId,
                                     Model model,
                                     Principal principal) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId.toString()));

        model.addAttribute("booking", booking);

        return "common/hotels/booking-details";
    }

}
package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.exception.RoomNotFoundException;
import com.damian.hotelbooking.exception.UserNotFoundException;
import com.damian.hotelbooking.service.HotelService;
import com.damian.hotelbooking.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "common/home";
    }

    @GetMapping("/become-a-host")
    public String showBecomeAHostForm() {
        return "common/become-a-host";
    }

    @PostMapping("/become-a-host")
    public String becomeAHost(Principal principal) {

        userService.assignHotelOwner(principal);
        return "redirect:/owner/hotels/new";

    }

}

package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.dto.BookingDto;
import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.entity.Booking;
import com.damian.hotelbooking.entity.Room;
import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.exception.BookingNotFoundException;
import com.damian.hotelbooking.repository.BookingRepository;
import com.damian.hotelbooking.repository.RoomRepository;
import com.damian.hotelbooking.service.*;
        import org.h2.engine.Mode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

        import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/hotels")
public class HotelController {

    private final HotelService hotelService;
    private final AmenityService amenityService;
    private final UserService userService;
    private final RoomService roomService;
    private final BookingService bookingService;

    public HotelController(HotelService hotelService,
                           AmenityService amenityService,
                           UserService userService,
                           RoomService roomService,
                           BookingService bookingService) {
        this.hotelService = hotelService;
        this.amenityService = amenityService;
        this.userService = userService;
        this.roomService = roomService;
        this.bookingService = bookingService;
    }

    @GetMapping("/list")
    public String listHotels(Model model) {

        model.addAttribute("hotels", hotelService.listHotels());
        model.addAttribute("allAmenities", amenityService.findAllAmenities());
        return "common/hotels/list";

    }

    @GetMapping("/{hotelId}")
    public String showHotelDetails(@PathVariable("hotelId") Long hotelId, Model model) {

        HotelDto hotelDto = hotelService.findById(hotelId);
        model.addAttribute("hotel", hotelDto);
        model.addAttribute("rooms", hotelDto.getRooms());
        return "common/hotels/hotel-details";

    }

    @GetMapping("/search")
    public String searchHotels(
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "amenities", required = false) List<String> amenities,
            @RequestParam(value = "capacity", defaultValue = "0") int capacity,
            @RequestParam(value = "roomType", required = false) String roomType,
            @RequestParam(value = "checkInDate", required = true) LocalDate checkInDate,
            @RequestParam(value = "checkOutDate", required = true) LocalDate checkOutDate,
            Model model) {

        model.addAttribute("hotels", hotelService.searchHotels(country, city, amenities, capacity, roomType, checkInDate, checkOutDate));
        model.addAttribute("allAmenities", amenityService.findAllAmenities());

        return "common/hotels/list";

    }

    @GetMapping("/{hotelId}/rooms/{roomId}/book")
    public String showCreateBookingForm(@PathVariable Long hotelId,
                                        @PathVariable Long roomId,
                                        Principal principal,
                                        Model model) {

        model.addAttribute("userId", userService.findByUsername(principal.getName()).getId());
        model.addAttribute("hotelId", hotelId);
        model.addAttribute("roomId", roomId);

        Room room = roomService.findById(roomId);
        model.addAttribute("roomPrice", room.getPrice());
        model.addAttribute("room", room);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setUserId(userService.findByUsername(principal.getName()).getId());
        bookingDto.setRoomId(roomId);

        model.addAttribute("booking", bookingDto);

        return "common/hotels/book";
    }

    @PostMapping("/{hotelId}/rooms/{roomId}/book")
    public String createBooking(@PathVariable Long hotelId,
                                @PathVariable Long roomId,
                                @ModelAttribute("booking") BookingDto bookingDto,
                                BindingResult bindingResult,
                                Principal principal,
                                Model model) {

        bookingService.createBooking(bookingDto, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("userId", userService.findByUsername(principal.getName()).getId());
            model.addAttribute("hotelId", hotelId);
            model.addAttribute("roomId", roomId);
            Room room = roomService.findById(roomId);
            model.addAttribute("roomPrice", room.getPrice());
            model.addAttribute("room", room);
            return "common/hotels/book";
        }

        return "redirect:/hotels/" + hotelId;
    }

}

package com.damian.hotelbooking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }

}

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
                              Model model,
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
                          Model model,
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

package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.dto.SignupDto;
import com.damian.hotelbooking.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showUserRegistrationForm(Model model) {

        model.addAttribute("signupDto", new SignupDto());
        return "auth/register";

    }

    @PostMapping
    public String userRegistration(@Valid @ModelAttribute("signupDto") SignupDto signupDto,
                                   BindingResult bindingResult) {

        userService.registerUser(signupDto, bindingResult);

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        return "redirect:/login";
    }
}
package com.damian.hotelbooking.controller;

import com.damian.hotelbooking.dto.PasswordDto;
import com.damian.hotelbooking.dto.ProfileDto;
import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

        import java.security.Principal;

@Controller
@RequestMapping("/account")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String viewProfile(Model model, Principal principal,
                              @RequestParam(value = "section", defaultValue = "profile") String section) {

        User user = userService.findByUsername(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("editable", false);
        model.addAttribute("activeSection", section);

        if ("security".equals(section)) {
            model.addAttribute("passwordDto", new PasswordDto());
        }

        return "common/account";
    }

    @GetMapping("/edit")
    public String editProfile(Model model, Principal principal) {

        model.addAttribute("profileDto", userService.getProfile(principal.getName()));
        model.addAttribute("editable", true);
        model.addAttribute("activeSection", "profile");
        return "common/account";

    }

    @PutMapping("/edit")
    public String editProfile(@Valid @ModelAttribute("profileDto") ProfileDto profileDto,
                              BindingResult bindingResult, Principal principal, Model model) {

        userService.saveProfile(profileDto, principal, bindingResult, model);

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userService.findByUsername(principal.getName()));
            model.addAttribute("editable", true);
            model.addAttribute("activeSection", "profile");
            return "common/account";
        }
        return "redirect:/account?section=profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("passwordDto") PasswordDto passwordDto,
                                 BindingResult bindingResult, Principal principal, Model model) {

        userService.changePassword(principal, passwordDto.getCurrentPassword(),
                passwordDto.getNewPassword(), passwordDto.getConfirmPassword(), bindingResult, model);

        if (bindingResult.hasErrors()) {
            model.addAttribute("activeSection", "security");
            return "common/account";
        }

        return "redirect:/account?section=security";
    }

    @DeleteMapping("/delete")
    public String deleteAccount(Principal principal, HttpServletRequest request) {

        userService.deleteAccount(principal, request);
        return "redirect:/";

    }

    @GetMapping("/notifications")
    public String notifications() {
        return "common/notifications";
    }

}
package com.damian.hotelbooking.dto;

import com.damian.hotelbooking.entity.BookingStatus;
import jakarta.validation.constraints.*;
        import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    @Min(value = 1, message = "Number of guests must be at least 1")
    @Max(value = 20, message = "Number of guests must be at most 20")
    private int numberOfGuests;

    @NotNull(message = "Booking status is required")
    private BookingStatus status;

    @DecimalMin(value = "0.0", inclusive = false, message = "Total price must be greater than 0")
    private double totalPrice;
}
package com.damian.hotelbooking.dto;

import com.damian.hotelbooking.entity.Amenity;
import com.damian.hotelbooking.entity.Room;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelDto {

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be at most 255 characters")
    private String name;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must be at most 100 characters")
    private String country;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must be at most 100 characters")
    private String city;

    @NotBlank(message = "Street is required")
    @Size(max = 255, message = "Street must be at most 255 characters")
    private String street;

    @Size(max = 20, message = "Postal code must be at most 20 characters")
    private String postalCode;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20, message = "Phone number must be at most 20 characters")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    private List<MultipartFile> images;

    private List<String> imageUrls;

    private Long ownerId;

    private Set<String> amenities;

    private Double pricePerNight;

    private Set<Room> rooms;
}


package com.damian.hotelbooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordDto {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}package com.damian.hotelbooking.dto;

import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProfileDto {

    @NotBlank
    @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers and underscores")
    private String username;

    @NotBlank
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "First name can only contain letters, spaces and hyphens")
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "Last name can only contain letters, spaces and hyphens")
    private String lastName;

    @NotBlank
    @Email(message = "Please provide a valid email address")
    @Size(min = 6, max = 100, message = "Email must be between 6 and 100 characters")
    private String email;

    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
    @Pattern(regexp = "^\\+?[0-9]+$", message = "Phone number can only contain digits and optional + prefix")
    private String phoneNumber;

}
package com.damian.hotelbooking.dto;

import com.damian.hotelbooking.entity.RoomType;
import jakarta.validation.constraints.*;
        import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {

    private Long id;

    @NotNull(message = "Hotel ID is required")
    private Long hotelId;

    @NotNull(message = "Room type is required")
    private RoomType type;

    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 50, message = "Capacity must be at most 50")
    private int capacity;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private double price;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    private boolean available;

    private String amenities;
}

package com.damian.hotelbooking.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupDto {

    @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers and underscores")
    private String username;

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "First name can only contain letters, spaces and hyphens")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "Last name can only contain letters, spaces and hyphens")
    private String lastName;

    @Email(message = "Please provide a valid email address")
    @Size(min = 6, max = 100, message = "Email must be between 6 and 100 characters")
    private String email;

    @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
    private String password;

    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
    @Pattern(regexp = "^\\+?[0-9]+$", message = "Phone number can only contain digits and optional + prefix")
    private String phoneNumber;

}
package com.damian.hotelbooking.entity;

import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;


}

package com.damian.hotelbooking.entity;

import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "number_of_guests", nullable = false)
    private int numberOfGuests;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;

    @Column(name = "total_price", nullable = false)
    private double totalPrice;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;
}

package com.damian.hotelbooking.entity;

public enum BookingStatus {
    CONFIRMED,
    CHECKED_IN,
    CHECKED_OUT,
    CANCELLED;
}

package com.damian.hotelbooking.entity;

import jakarta.persistence.*;
        import lombok.*;
        import java.util.Set;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "email",nullable = false)
    private String email;

    @Column(name = "description")
    private String description;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "postal_code")
    private String postalCode;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    private Set<Room> rooms;

    @ManyToMany
    @JoinTable(
            name = "hotel_amenities",
            joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private Set<Amenity> amenities;

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Hotel hotel = (Hotel) object;
        return Objects.equals(id, hotel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
package com.damian.hotelbooking.entity;

import jakarta.persistence.*;
        import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String url;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;
}

package com.damian.hotelbooking.entity;

import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hotel", nullable = false)
    private Hotel hotel;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private RoomType type;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "description")
    private String description;

    @Column(name = "available", nullable = false)
    private boolean available;

    @OneToMany(mappedBy = "room")
    private Set<Booking> bookings;

    @ManyToMany
    @JoinTable(
            name = "room_amenities",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private Set<Amenity> amenities;

}

package com.damian.hotelbooking.entity;

public enum RoomType {
    SINGLE,
    DOUBLE,
    SUITE,
    DELUXE,
    FAMILY,
    PENTHOUSE;
}

package com.damian.hotelbooking.entity;

import jakarta.persistence.*;
        import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    public User(String username, String firstName, String lastName, String email, String password, String phoneNumber, UserRole role) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public String getFriendlyRole() {
        if (role == null) return "";

        switch (role) {
            case ROLE_HOTEL_ADMIN:
                return "Hotel Admin";
            case ROLE_USER:
                return "User";
            case ROLE_PLATFORM_ADMIN:
                return "Administrator";
            default:
                return role.name().replace("ROLE_", "").replace("_", " ");
        }
    }

}
package com.damian.hotelbooking.entity;

public enum UserRole {
    ROLE_USER,
    ROLE_HOTEL_ADMIN,
    ROLE_PLATFORM_ADMIN;
}

package com.damian.hotelbooking.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(String message) {
        super(message);
    }
}

package com.damian.hotelbooking.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(String message) {
        super(message);
    }
}

package com.damian.hotelbooking.exception;

public class HotelNotFoundException extends RuntimeException {
    public HotelNotFoundException(String message) {
        super(message);
    }
}

package com.damian.hotelbooking.exception;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(String message) {
        super(message);
    }
}

package com.damian.hotelbooking.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}

package com.damian.hotelbooking.repository;

import com.damian.hotelbooking.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    Optional<Amenity> findByName(String name);
}

package com.damian.hotelbooking.repository;

import com.damian.hotelbooking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByRoomId(Long roomId);

    List<Booking> findByUserId(Long id);

}

package com.damian.hotelbooking.repository;

import com.damian.hotelbooking.entity.HotelImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelImageRepository extends JpaRepository<HotelImage, Long> {

    List<HotelImage> findAllByHotelId(Long hotelId);

}

package com.damian.hotelbooking.repository;

import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findAllByOwnerId(Long ownerId);

}

package com.damian.hotelbooking.repository;

import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.dto.RoomDto;
import com.damian.hotelbooking.entity.Hotel;
import com.damian.hotelbooking.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.validation.BindingResult;

public interface RoomRepository extends JpaRepository<Room, Long> {

}

package com.damian.hotelbooking.repository;

import com.damian.hotelbooking.entity.User;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    public List<User> findAllByOrderByLastNameAsc();

    Optional<User> findByUsername(String username);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}

package com.damian.hotelbooking.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery("select username, password, true as enabled from user where username = ?");
        manager.setAuthoritiesByUsernameQuery("select username, role as authority from user where username = ?");
        return manager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(configurer ->
                        configurer
                                .requestMatchers("/users/**").hasRole("PLATFORM_ADMIN")
                                .requestMatchers("/owner/**").hasAnyRole("HOTEL_ADMIN", "PLATFORM_ADMIN")
                                .requestMatchers("/account").authenticated()
                                .requestMatchers("/become-a-host").authenticated()
                                .requestMatchers("/bookings").authenticated()
                                .requestMatchers("/hotels/*/rooms/*/book").authenticated()
                                .anyRequest().permitAll()
                )
                .formLogin(form ->
                        form
                                .loginPage("/login")
                                .loginProcessingUrl("/authenticate")
                                .permitAll()
                )
                .logout(logout -> logout.permitAll())
                .exceptionHandling(configurer -> configurer.accessDeniedPage("/access-denied"));

        return http.build();
    }
}


package com.damian.hotelbooking.service;

import java.util.List;

public interface AmenityService {

    List<String> findAllAmenities();


}

package com.damian.hotelbooking.service;

import com.damian.hotelbooking.entity.Amenity;
import com.damian.hotelbooking.repository.AmenityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepository amenityRepository;

    public AmenityServiceImpl(AmenityRepository amenityRepository) {
        this.amenityRepository = amenityRepository;
    }

    public List<String> findAllAmenities() {

        return amenityRepository.findAll()
                .stream()
                .map(Amenity::getName)
                .toList();

    }

}

package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.BookingDto;
import org.springframework.validation.BindingResult;

public interface BookingService {
    void createBooking(BookingDto bookingDto, BindingResult bindingResult);

    void cancelBooking(Long bookingId);
}

package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.BookingDto;
import com.damian.hotelbooking.entity.Booking;
import com.damian.hotelbooking.entity.BookingStatus;
import com.damian.hotelbooking.entity.Room;
import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.exception.RoomNotFoundException;
import com.damian.hotelbooking.exception.UserNotFoundException;
import com.damian.hotelbooking.repository.BookingRepository;
import com.damian.hotelbooking.repository.RoomRepository;
import com.damian.hotelbooking.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public BookingServiceImpl(UserRepository userRepository, RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public void createBooking(BookingDto bookingDto, BindingResult bindingResult) {

        User user = userRepository.findById(bookingDto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(bookingDto.getUserId().toString()));
        Room room = roomRepository.findById(bookingDto.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException(bookingDto.getRoomId().toString()));

        Long ownerId = room.getHotel().getOwner().getId();

        long nights = java.time.temporal.ChronoUnit.DAYS.between(bookingDto.getCheckInDate(),
                bookingDto.getCheckOutDate());

        if (nights > 30) {
            bindingResult.rejectValue("checkOutDate", "booking.tooLong", "Cannot book for more than 30 days.");
        }

        if (bookingDto.getUserId().equals(ownerId)) {
            bindingResult.reject("owner.booking", "Owners can't book their own listings.");
        }

        if (bookingDto.getCheckInDate().isAfter(bookingDto.getCheckOutDate())) {
            bindingResult.rejectValue("checkOutDate", "booking.illegalCheckInOut", "Cannot check in after check out.");
        }

        if (bookingDto.getCheckInDate().isEqual(bookingDto.getCheckOutDate())) {
            bindingResult.rejectValue("checkOutDate", "booking.oneNight", "Must book at least one night.");
        }

        if (bookingDto.getNumberOfGuests() > room.getCapacity()) {
            bindingResult.rejectValue("numberOfGuests", "booking.capacity", "Capacity reached.");
        }

        List<Booking> existingBookings = bookingRepository.findByRoomId(room.getId());
        boolean overlaps = existingBookings.stream().anyMatch(b ->
                !bookingDto.getCheckOutDate().isBefore(b.getCheckInDate()) && !bookingDto.getCheckInDate().isAfter(b.getCheckOutDate())
        );
        if (overlaps) {
            bindingResult.rejectValue("checkInDate", "booking.overlap", "Selected dates overlap with an existing booking.");
        }

        if (bindingResult.hasErrors()) {
            return;
        }

        double totalPrice = room.getPrice() * nights;

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(bookingDto.getCheckInDate());
        booking.setCheckOutDate(bookingDto.getCheckOutDate());
        booking.setNumberOfGuests(bookingDto.getNumberOfGuests());
        booking.setStatus(BookingStatus.CHECKED_IN);
        booking.setTotalPrice(totalPrice);

        bookingRepository.save(booking);

    }

    @Override
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new RoomNotFoundException(bookingId.toString())
        );

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }
}

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


package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.entity.Hotel;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

public interface HotelService {

    void saveHotel(HotelDto hotelDto, BindingResult bindingResult, Principal principal);

    List<HotelDto> listHotels();

    HotelDto findById(Long hotelId);

    List<HotelDto> searchHotels(String country, String city, List<String> amenities, int capacity,
                                String roomType, LocalDate checkInDate, LocalDate checkOutDate);

    HotelDto toHotelDto(Hotel hotel);

    Hotel toHotel(HotelDto hotelDto);

    List<HotelDto> findAllByOwnerId(Principal principal);

    void checkOwnership(Long hotelId, Principal principal);
}


package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.entity.Amenity;
import com.damian.hotelbooking.entity.Hotel;
import com.damian.hotelbooking.entity.HotelImage;
import com.damian.hotelbooking.entity.Room;
import com.damian.hotelbooking.exception.HotelNotFoundException;
import com.damian.hotelbooking.exception.UserNotFoundException;
import com.damian.hotelbooking.repository.AmenityRepository;
import com.damian.hotelbooking.repository.HotelRepository;
import com.damian.hotelbooking.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
        import java.util.stream.Collectors;

@Service
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final AmenityRepository amenityRepository;
    private final HotelImageService hotelImageService;

    public HotelServiceImpl(HotelRepository hotelRepository,
                            UserService userService,
                            UserRepository userRepository,
                            AmenityRepository amenityRepository, HotelImageService hotelImageService) {
        this.hotelRepository = hotelRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.amenityRepository = amenityRepository;
        this.hotelImageService = hotelImageService;
    }

    @Override
    public void saveHotel(HotelDto hotelDto, BindingResult bindingResult, Principal principal) {

        if (bindingResult.hasErrors()) {
            return;
        }

        Long currentUserId = null;
        if (principal != null) {
            currentUserId = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new UserNotFoundException(principal.getName()))
                    .getId();
            hotelDto.setOwnerId(currentUserId);
        }

        Hotel hotel;
        if (hotelDto.getId() != null) {
            hotel = hotelRepository.findById(hotelDto.getId())
                    .orElseThrow(() -> new HotelNotFoundException(hotelDto.getId().toString()));

            if (!hotel.getOwner().getId().equals(currentUserId)) {
                throw new AccessDeniedException("You are not the owner of this hotel");
            }

        } else {
            hotel = new Hotel();
        }

        hotel.setName(hotelDto.getName());
        hotel.setCity(hotelDto.getCity());
        hotel.setCountry(hotelDto.getCountry());
        hotel.setStreet(hotelDto.getStreet());
        hotel.setPostalCode(hotelDto.getPostalCode());
        hotel.setPhoneNumber(hotelDto.getPhoneNumber());
        hotel.setEmail(hotelDto.getEmail());
        hotel.setDescription(hotelDto.getDescription());
        hotel.setOwner(userService.findById(hotelDto.getOwnerId()));

        Set<String> amenities = hotelDto.getAmenities();
        if (amenities == null) amenities = Collections.emptySet();

        Set<Amenity> amenitySet = amenities.stream()
                .filter(name -> !name.trim().isEmpty())
                .map(name -> amenityRepository.findByName(name).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        hotel.setAmenities(amenitySet);

        hotelRepository.save(hotel);

        if (hotelDto.getImages() != null && !hotelDto.getImages().isEmpty()) {
            hotelImageService.saveImages(hotelDto.getImages(), hotel);
        }

    }

    @Override
    public List<HotelDto> listHotels() {
        return hotelRepository.findAll()
                .stream()
                .map(this::toHotelDto)
                .toList();
    }

    @Override
    public HotelDto findById(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId.toString()));

        return toHotelDto(hotel);
    }

    @Override
    public List<HotelDto> searchHotels(String country, String city, List<String> amenities, int capacity,
                                       String roomType, LocalDate checkInDate, LocalDate checkOutDate) {
        return hotelRepository.findAll()
                .stream()
                .filter(hotel -> country == null || country.isBlank() || hotel.getCountry().equalsIgnoreCase(country))
                .filter(hotel -> city == null || city.isBlank() || hotel.getCity().equalsIgnoreCase(city))
                .filter(hotel -> {
                    if (amenities == null || amenities.isEmpty()) return true;
                    Set<String> hotelAmenities = hotel.getAmenities()
                            .stream()
                            .map(Amenity::getName)
                            .collect(Collectors.toSet());
                    return hotelAmenities.containsAll(amenities);
                })
                .map(hotel -> {
                    Set<Room> filteredRooms = hotel.getRooms().stream()
                            .filter(room -> room.getCapacity() >= capacity)
                            .filter(room -> roomType == null || roomType.isBlank() ||
                                    room.getType().name().equalsIgnoreCase(roomType))
                            .filter(room -> room.getBookings().stream().noneMatch(booking ->
                                    checkInDate.isBefore(booking.getCheckOutDate()) &&
                                            checkOutDate.isAfter(booking.getCheckInDate())
                            ))
                            .collect(Collectors.toSet());
                    hotel.setRooms(filteredRooms);
                    return hotel;
                })
                .filter(hotel -> !hotel.getRooms().isEmpty())
                .map(this::toHotelDto)
                .collect(Collectors.toList());
    }


    @Override
    public List<HotelDto> findAllByOwnerId(Principal principal) {
        Long ownerId = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException(principal.getName()))
                .getId();

        List<Hotel> hotels = hotelRepository.findAllByOwnerId(ownerId);
        return hotels.stream()
                .map(this::toHotelDto)
                .toList();
    }

    @Override
    public void checkOwnership(Long hotelId, Principal principal) {
        HotelDto hotelDto = findById(hotelId);
        if (!hotelDto.getOwnerId().equals(userService.findIdByUsername(principal.getName()))) {
            throw new AccessDeniedException("You are not the owner of this hotel");
        }
    }

    // Mappere HotelDto <-> Hotel

    @Override
    public HotelDto toHotelDto(Hotel hotel) {
        HotelDto hotelDto = new HotelDto();
        hotelDto.setOwnerId(hotel.getOwner().getId());
        hotelDto.setId(hotel.getId());
        hotelDto.setName(hotel.getName());
        hotelDto.setCountry(hotel.getCountry());
        hotelDto.setCity(hotel.getCity());
        hotelDto.setPostalCode(hotel.getPostalCode());
        hotelDto.setPhoneNumber(hotel.getPhoneNumber());
        hotelDto.setEmail(hotel.getEmail());
        hotelDto.setStreet(hotel.getStreet());
        hotelDto.setDescription(hotel.getDescription());
        Set<String> amenities = hotel.getAmenities().stream()
                .map(Amenity::getName)
                .collect(Collectors.toSet());
        hotelDto.setAmenities(amenities);
        hotelDto.setRooms(hotel.getRooms());

        if (hotel.getRooms() != null && !hotel.getRooms().isEmpty()) {
            hotelDto.setPricePerNight(
                    hotel.getRooms().stream()
                            .filter(Room::isAvailable)
                            .map(Room::getPrice)
                            .min(Double::compare)
                            .orElse(null)
            );
        }

        List<String> imageUrls = hotelImageService.getImagesByHotel(hotel.getId())
                .stream()
                .map(HotelImage::getUrl)
                .collect(Collectors.toList());
        hotelDto.setImageUrls(imageUrls);


        return hotelDto;
    }

    @Override
    public Hotel toHotel(HotelDto hotelDto) {
        Hotel hotel = new Hotel();
        hotel.setName(hotelDto.getName());
        hotel.setCity(hotelDto.getCity());
        hotel.setCountry(hotelDto.getCountry());
        hotel.setStreet(hotelDto.getStreet());
        hotel.setPostalCode(hotelDto.getPostalCode());
        hotel.setPhoneNumber(hotelDto.getPhoneNumber());
        hotel.setEmail(hotelDto.getEmail());
        hotel.setDescription(hotelDto.getDescription());
        hotel.setOwner(userService.findById(hotelDto.getOwnerId()));

        return hotel;
    }
}

package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.RoomDto;
import com.damian.hotelbooking.entity.Room;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

public interface RoomService {

    void addRoom(Long hotelId, RoomDto roomDto, BindingResult bindingResult, Principal principal);

    List<LocalDate[]> getUnavailableDateRanges(Long roomId);

    Room findById(Long roomId);
}

package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.HotelDto;
import com.damian.hotelbooking.dto.RoomDto;
import com.damian.hotelbooking.entity.*;
        import com.damian.hotelbooking.exception.HotelNotFoundException;
import com.damian.hotelbooking.exception.RoomNotFoundException;
import com.damian.hotelbooking.repository.AmenityRepository;
import com.damian.hotelbooking.repository.BookingRepository;
import com.damian.hotelbooking.repository.HotelRepository;
import com.damian.hotelbooking.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
        import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {

    private final AmenityRepository amenityRepository;
    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final HotelService hotelService;
    private final BookingRepository bookingRepository;

    public RoomServiceImpl(AmenityRepository amenityRepository,
                           RoomRepository roomRepository,
                           HotelRepository hotelRepository,
                           HotelService hotelService, BookingRepository bookingRepository) {
        this.amenityRepository = amenityRepository;
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.hotelService = hotelService;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public void addRoom(Long hotelId, RoomDto roomDto, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) return;

        roomDto.setHotelId(hotelId);

        hotelService.checkOwnership(hotelId, principal);

        Room room = new Room();
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException(hotelId.toString()));
        room.setHotel(hotel);

        room.setDescription(roomDto.getDescription());
        room.setCapacity(roomDto.getCapacity());
        room.setPrice(roomDto.getPrice());
        room.setType(roomDto.getType());
        room.setAvailable(roomDto.isAvailable());

        Set<Amenity> amenitySet = Collections.emptySet();
        if (roomDto.getAmenities() != null && !roomDto.getAmenities().isBlank()) {
            amenitySet = Arrays.stream(roomDto.getAmenities().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(name -> amenityRepository.findByName(name).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }

        room.setAmenities(amenitySet);

        roomRepository.save(room);
    }

    @Override
    public List<LocalDate[]> getUnavailableDateRanges(Long roomId) {
        List<Booking> bookings = bookingRepository.findByRoomId(roomId);
        return bookings.stream()
                .filter(b -> b.getStatus() != BookingStatus.CANCELLED)
                .map(b -> new LocalDate[]{b.getCheckInDate(), b.getCheckOutDate()})
                .toList();
    }


    @Override
    public Room findById(Long roomId) {

        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId.toString()));

    }

}

package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.ProfileDto;
import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.dto.SignupDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.util.List;


public interface UserService {

    List<User> findAll();

    void registerUser(SignupDto signupDto, BindingResult bindingResult);

    void saveProfile(ProfileDto profileDto, Principal principal,
                     BindingResult bindingResult, Model model);

    User findById(Long theId);

    void save(User user);

    boolean changePassword(Principal principal, String currentPassword, String newPassword,
                           String confirmPassword, BindingResult bindingResult, Model model);

    void deleteAccount(Principal principal, HttpServletRequest request);

    User findByUsername(String name);

    ProfileDto getProfile(String name);

    void assignHotelOwner(Principal principal);

    void deleteById(Long userId);

    Long findIdByUsername(String name);
}


package com.damian.hotelbooking.service;

import com.damian.hotelbooking.dto.ProfileDto;
import com.damian.hotelbooking.dto.SignupDto;
import com.damian.hotelbooking.entity.User;
import com.damian.hotelbooking.exception.UserNotFoundException;
import com.damian.hotelbooking.repository.UserRepository;
import com.damian.hotelbooking.entity.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static com.damian.hotelbooking.entity.UserRole.ROLE_HOTEL_ADMIN;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void registerUser(@Valid SignupDto signupDto, BindingResult bindingResult) {

        if (userRepository.existsByEmail(signupDto.getEmail())) {
            bindingResult.rejectValue("email", "error.signupDto", "Email already registered");
        }

        if (userRepository.existsByUsername(signupDto.getUsername())) {
            bindingResult.rejectValue("username", "error.signupDto", "Username already taken");
        }

        if (userRepository.existsByPhoneNumber(signupDto.getPhoneNumber())) {
            bindingResult.rejectValue("phoneNumber", "error.signupDto", "Phone number already in use");
        }

        if (bindingResult.hasErrors()) {
            return;
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(signupDto.getPassword());

        User user = new User(
                signupDto.getUsername(),
                signupDto.getFirstName(),
                signupDto.getLastName(),
                signupDto.getEmail(),
                encodedPassword,
                signupDto.getPhoneNumber(),
                UserRole.ROLE_USER
        );

        userRepository.save(user);
    }

    @Override
    public void saveProfile(@Valid @ModelAttribute("profileDto") ProfileDto profileDto, Principal principal,
                            BindingResult bindingResult, Model model) {

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException(principal.getName()));

        if (!user.getPhoneNumber().equals(profileDto.getPhoneNumber()) &&
                userRepository.existsByPhoneNumber(profileDto.getPhoneNumber())) {
            bindingResult.rejectValue("phoneNumber", "error.profileDto", "Phone number already in use");
        }

        if (!user.getEmail().equals(profileDto.getEmail()) &&
                userRepository.existsByEmail(profileDto.getEmail())) {
            bindingResult.rejectValue("email", "error.profileDto", "Email already registered");
        }

        if (!user.getUsername().equals(profileDto.getUsername()) &&
                userRepository.existsByUsername(profileDto.getUsername())) {
            bindingResult.rejectValue("username", "error.profileDto", "Username already in use");
        }

        if (bindingResult.hasErrors()) {
            return;
        }

        user.setUsername(profileDto.getUsername());
        user.setFirstName(profileDto.getFirstName());
        user.setLastName(profileDto.getLastName());
        user.setEmail(profileDto.getEmail());
        user.setPhoneNumber(profileDto.getPhoneNumber());

        userRepository.save(user);
    }

    @Override
    public void save(User user) {
        if (user.getPassword() != null && !user.getPassword().isBlank() && !user.getPassword().startsWith("{bcrypt}")) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            user.setPassword("{bcrypt}" + passwordEncoder.encode(user.getPassword()));
        } else if (user.getPassword() == null || user.getPassword().isBlank()) {
            User existing = userRepository.findById(user.getId()).orElseThrow();
            user.setPassword(existing.getPassword());
        }
        userRepository.save(user);
    }

    @Override
    public boolean changePassword(Principal principal, String currentPassword, String newPassword,
                                  String confirmPassword, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("activeSection", "security");
            return false;
        }

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UserNotFoundException(principal.getName()));

        if (!newPassword.equals(confirmPassword)) {
            bindingResult.rejectValue("confirmPassword", "error.passwordDto", "New passwords don't match");
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String currentEncodedPassword = user.getPassword().replace("{bcrypt}", "");
        if (!passwordEncoder.matches(currentPassword, currentEncodedPassword)) {
            bindingResult.rejectValue("currentPassword", "error.passwordDto", "Current password is incorrect");
            return false;
        }

        user.setPassword("{bcrypt}" + passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    @Override
    public void deleteAccount(Principal principal, HttpServletRequest request) {
        User user = findByUsername(principal.getName());

        Long userId = user.getId();

        userRepository.deleteById(userId);

        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    @Override
    public ProfileDto getProfile(String name) {
        User user = findByUsername(name);

        return new ProfileDto(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber()
        );
    }

    @Override
    public void assignHotelOwner(Principal principal) {

        User user = findByUsername(principal.getName());

        user.setRole(ROLE_HOTEL_ADMIN);
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername()); // ia user's info
        UsernamePasswordAuthenticationToken newAuth =
                new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
                        userDetails.getAuthorities()); // logheaza utilizatorul w the updated info (role)
        SecurityContextHolder.getContext().setAuthentication(newAuth); // refresh sesiune/auth propriu zis

    }

    @Override
    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public Long findIdByUsername(String name) {
        User user = findByUsername(name);
        return user.getId();
    }

    // Helper methods

    @Override
    public User findByUsername(String name) {
        return userRepository.findByUsername(name).orElseThrow(() ->
                new UserNotFoundException(name));
    }

    @Override
    public User findById(Long theId) {
        Optional<User> result = userRepository.findById(theId);

        User user;
        if (result.isPresent()) {
            user = result.get();
        } else {
            throw new UserNotFoundException(theId.toString());
        }

        return user;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAllByOrderByLastNameAsc();
    }
}

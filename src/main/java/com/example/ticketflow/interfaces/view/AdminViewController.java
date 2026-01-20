package com.example.ticketflow.interfaces.view;

import com.example.ticketflow.application.SeatFacade;
import com.example.ticketflow.application.dto.SeatStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminViewController {

    private final SeatFacade seatFacade;

    // RequestMapping 같은 역할
    @GetMapping("/admin/seats")
    public String seatList(@RequestParam(defaultValue = "1") Long hallId, Model model) {

        List<SeatStatusDto> seats = seatFacade.getAllSeats(hallId);
        model.addAttribute("seats", seats);

        return "seat-list";
    }
}

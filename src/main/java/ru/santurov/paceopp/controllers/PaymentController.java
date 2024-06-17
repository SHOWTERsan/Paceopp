package ru.santurov.paceopp.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.santurov.paceopp.models.Audio;
import ru.santurov.paceopp.models.Beat;
import ru.santurov.paceopp.models.User;
import ru.santurov.paceopp.services.BeatService;
import ru.santurov.paceopp.services.EmailService;
import ru.santurov.paceopp.services.OrderService;
import ru.santurov.paceopp.services.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class PaymentController {
    private final EmailService emailService;
    private final BeatService beatService;
    private final UserService userService;
    private final OrderService orderService;

    @GetMapping("/payment")
    public String getPayment() {
        return "payment";
    }

    @PostMapping("/payment/success")
    public String handlePaymentSuccess(@RequestParam Long serviceId, @RequestParam int beatId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Beat beat = beatService.findById(beatId).orElseThrow(() -> new RuntimeException("Beat not found"));
        try {
            orderService.saveOrder(username, serviceId, beat);
        } catch (Exception e) {
            e.printStackTrace();

            return "redirect:/payment?error=true";
        }

        String email = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found")).getEmail();
        List<Audio> audioFiles = getAudioFilesByServiceId(beat, serviceId);

        String subject = "Ваши биты";
        String text = "Спасибо за вашу покупку. Ваши файлы во вложении.";

        emailService.sendMessage(subject, text, email, audioFiles);


        return "redirect:/payment";
    }

    private List<Audio> getAudioFilesByServiceId(Beat beat, Long serviceId) {
        return beat.getAudioFiles().stream()
                .filter(audio -> serviceId == 16 ? "mp3".equals(audio.getFileFormat()) : List.of("mp3", "wav").contains(audio.getFileFormat()))
                .collect(Collectors.toList());
    }
}

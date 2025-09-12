package org.innowise.internship.userservice.UserService.controllers;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoCreateDTO;
import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoUpdateDTO;
import org.innowise.internship.userservice.UserService.services.cardinfo.CardInfoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cardinfo")
public class CardInfoController {

    private final CardInfoService cardInfoService;

    private Long getIdFromAuthentication()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long id = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String userId = userDetails.getUsername(); id = Long.parseLong(userId);
        }
        return id;
    }

    private Long getUserIdFromHeader(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader == null) {
            throw new IllegalArgumentException("X-User-Id header is missing");
        }
        try {
            return Long.parseLong(userIdHeader);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid X-User-Id header: " + userIdHeader);
        }
    }

    @PostMapping
    public ResponseEntity<CardInfoResponseDTO> createCardInfo(
            @RequestBody @Valid CardInfoCreateDTO cardInfoCreateDTO,
            HttpServletRequest request) {

        Long userId = getUserIdFromHeader(request);
        CardInfoResponseDTO cardInfoResponseDTO = cardInfoService.createCard(cardInfoCreateDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(cardInfoResponseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardInfoResponseDTO> getCardInfoById(
            @PathVariable Long id,
            HttpServletRequest request) {

        Long userId = getUserIdFromHeader(request);
        CardInfoResponseDTO cardInfoResponseDTO = cardInfoService.getCardById(id, userId);
        return ResponseEntity.ok(cardInfoResponseDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CardInfoResponseDTO> updateCardInfoById(
            @PathVariable Long id,
            @RequestBody @Valid CardInfoUpdateDTO cardInfoUpdateDTO,
            HttpServletRequest request) {

        Long userId = getUserIdFromHeader(request);
        CardInfoResponseDTO cardInfoResponseDTO = cardInfoService.updateCard(id, cardInfoUpdateDTO, userId);
        return ResponseEntity.ok(cardInfoResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCardInfoById(
            @PathVariable Long id,
            HttpServletRequest request) {

        Long userId = getUserIdFromHeader(request);
        cardInfoService.deleteCardById(id, userId);
        return ResponseEntity.noContent().build();
    }
}

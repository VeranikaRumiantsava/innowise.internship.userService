package org.innowise.internship.userservice.UserService.controllers;

import java.util.List;

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

    @PostMapping
    public ResponseEntity<CardInfoResponseDTO> createCardInfo(@RequestBody @Valid CardInfoCreateDTO cardInfoCreateDTO) {

        CardInfoResponseDTO cardInfoResponseDTO = cardInfoService.createCard(cardInfoCreateDTO, getIdFromAuthentication());
        return ResponseEntity.status(HttpStatus.CREATED).body(cardInfoResponseDTO);
    }


    //???
    @GetMapping("/{id}")
    public ResponseEntity<CardInfoResponseDTO> getCardInfoById(@PathVariable Long id) {
        CardInfoResponseDTO cardInfoResponseDTO = cardInfoService.getCardById(id, getIdFromAuthentication());
        return ResponseEntity.ok(cardInfoResponseDTO);
    }

    @GetMapping("/ids")
    public ResponseEntity<List<CardInfoResponseDTO>> getCardInfoListByIds(@RequestParam List<Long> ids) {
        List<CardInfoResponseDTO> cards = cardInfoService.getCardsByIds(ids);
        return ResponseEntity.ok(cards);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CardInfoResponseDTO> updateCardInfoById(@PathVariable Long id, @RequestBody @Valid CardInfoUpdateDTO cardInfoUpdateDTO) {
        CardInfoResponseDTO cardInfoResponseDTO = cardInfoService.updateCard(id, cardInfoUpdateDTO, getIdFromAuthentication());
        return ResponseEntity.ok(cardInfoResponseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCardInfoById(@PathVariable Long id) {
        cardInfoService.deleteCardById(id, getIdFromAuthentication());
        return ResponseEntity.noContent().build();
    }
}

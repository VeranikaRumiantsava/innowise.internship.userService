package org.innowise.internship.userservice.UserService.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoFullDTO;
import org.innowise.internship.userservice.UserService.dto.cardInfo.CardInfoUpdateDTO;
import org.innowise.internship.userservice.UserService.services.CardInfoService;

import lombok.RequiredArgsConstructor;




@RestController
@RequiredArgsConstructor
@RequestMapping("/cardinfo")
public class CardInfoController {

    private final CardInfoService cardInfoService;

    @PostMapping
    public ResponseEntity<CardInfoFullDTO> createCardInfo(@RequestBody @Valid CardInfoCreateDTO cardInfoCreateDTO) {
        CardInfoFullDTO cardInfoFullDTO = cardInfoService.createCard(cardInfoCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(cardInfoFullDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardInfoFullDTO> getCardInfoById(@PathVariable Long id) {
        CardInfoFullDTO cardInfoFullDTO = cardInfoService.getCardById(id);
        return ResponseEntity.ok(cardInfoFullDTO);
    }

    @GetMapping("/ids")
    public ResponseEntity<List<CardInfoFullDTO>> getCardInfoListByIds(@RequestParam List<Long> ids) {
        List<CardInfoFullDTO> cards = cardInfoService.getCardsByIds(ids);
        return ResponseEntity.ok(cards);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CardInfoFullDTO> updateCardInfo(@PathVariable Long id, @RequestBody @Valid CardInfoUpdateDTO cardInfoUpdateDTO) {
        CardInfoFullDTO cardInfoFullDTO = cardInfoService.updateCard(id, cardInfoUpdateDTO);
        return ResponseEntity.ok(cardInfoFullDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCardInfo(@PathVariable Long id) {
        cardInfoService.deleteCardById(id);
        return ResponseEntity.noContent().build();
    }
}

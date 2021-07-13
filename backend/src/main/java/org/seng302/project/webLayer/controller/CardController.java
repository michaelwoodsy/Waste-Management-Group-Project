package org.seng302.project.webLayer.controller;

import org.seng302.project.serviceLayer.dto.card.CreateCardDTO;
import org.seng302.project.serviceLayer.dto.card.CreateCardResponseDTO;
import org.seng302.project.serviceLayer.dto.card.EditCardDTO;
import org.seng302.project.serviceLayer.dto.card.GetCardResponseDTO;
import org.seng302.project.serviceLayer.service.CardService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for handling requests to do with marketplace cards.
 */
@RestController
public class CardController {

    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    /**
     * Creates a new card
     *
     * @param dto     DTO containing new card parameters
     * @param appUser user who is making the request
     * @return a response DTO containing the ID of the newly created card
     */
    @PostMapping("/cards")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCardResponseDTO createCard(@RequestBody @Valid CreateCardDTO dto, @AuthenticationPrincipal AppUserDetails appUser) {
        return cardService.createCard(dto, appUser);
    }

    /**
     * Gets a single card with id cardID.
     *
     * @param id of the card to retrieve.
     * @return a single card object.
     */
    @GetMapping("/cards/{id}")
    @ResponseStatus(HttpStatus.OK)
    public GetCardResponseDTO getCard(@PathVariable int id) {
        return cardService.getCard(id);
    }

    /**
     * Endpoint for getting all cards in a section.
     *
     * @param section Section to get all cards from.
     * @return List of Cards in the corresponding section.
     */
    @GetMapping("/cards")
    public List<GetCardResponseDTO> getAllCards(@RequestParam String section) {
        return cardService.getAllCardsForSection(section);
    }

    /**
     * Extends the card with id cardID's displayPeriodEnd date by 14 days.
     *
     * @param id of the card to extend.
     */
    @PutMapping("/cards/{id}/extenddisplayperiod")
    @ResponseStatus(HttpStatus.OK)
    public void extendCardDisplayPeriod(@PathVariable int id, @AuthenticationPrincipal AppUserDetails appUser) {
        cardService.extendCardDisplayPeriod(id, appUser);
    }

    /**
     * Endpoint for deleting a card
     *
     * @param id      id of the card to be deleted
     * @param appUser user details to check if the current user is allowed to delete this card
     */
    @DeleteMapping("/cards/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCard(@PathVariable int id, @AuthenticationPrincipal AppUserDetails appUser) {
        cardService.deleteCard(id, appUser);
    }

    /**
     * Endpoint to get all cards from a user
     *
     * @param id id of the user
     * @return List of cards from that user
     */
    @GetMapping("/users/{id}/cards")
    @ResponseStatus(HttpStatus.OK)
    public List<GetCardResponseDTO> getAllCardsByUser(@PathVariable Integer id) {
        return cardService.getAllCardsByUser(id);
    }

    /**
     * Endpoint to edit a card
     *
     * @param id of the card to extend.
     */
    @PutMapping("/cards/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void editCard(@PathVariable int id, @RequestBody @Valid EditCardDTO dto, @AuthenticationPrincipal AppUserDetails appUser) {
        cardService.editCard(id, dto, appUser);
    }

    /**
     * Endpoint to get all cards that fit the search query
     * @param section The section to search by
     * @param keywordIds The list of keyword IDs to search by
     * @param union Option to match the search with all or some of the inputs
     * @return List of cards that fit the search criteria
     */
    @GetMapping("/cards/search")
    @ResponseStatus(HttpStatus.OK)
    public List<GetCardResponseDTO> searchCards(
            @RequestParam String section,
            @RequestParam List<Integer> keywordIds,
            @RequestParam Boolean union
    ) {
        return cardService.searchCards(section, keywordIds, union);
    }

}
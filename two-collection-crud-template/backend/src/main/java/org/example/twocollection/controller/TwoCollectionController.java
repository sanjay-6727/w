package org.example.twocollection.controller;

import org.example.twocollection.model.FirstItem;
import org.example.twocollection.model.SecondItem;
import org.example.twocollection.service.TwoCollectionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class TwoCollectionController {
    private final TwoCollectionService service;

    public TwoCollectionController(TwoCollectionService service) {
        this.service = service;
    }

    @GetMapping("/first-items")
    public List<FirstItem> getFirstItems() {
        return service.getFirstItems();
    }

    @PostMapping("/first-items")
    public FirstItem addFirstItem(@RequestBody FirstItem item) {
        return service.addFirstItem(item);
    }

    @PutMapping("/first-items/{id}")
    public FirstItem updateFirstItem(@PathVariable String id, @RequestBody FirstItem item) {
        return service.updateFirstItem(id, item);
    }

    @DeleteMapping("/first-items/{id}")
    public void deleteFirstItem(@PathVariable String id) {
        service.deleteFirstItem(id);
    }

    @GetMapping("/first-items/search")
    public List<FirstItem> searchFirstItems(@RequestParam String name) {
        return service.searchFirstItems(name);
    }

    @GetMapping("/first-items/category/{category}")
    public List<FirstItem> filterByCategory(@PathVariable String category) {
        return service.filterByCategory(category);
    }

    @GetMapping("/first-items/sort")
    public List<FirstItem> sortByPrice(@RequestParam(defaultValue = "false") boolean desc) {
        return service.sortByPrice(desc);
    }

    @GetMapping("/first-items/with-related")
    public List<FirstItem> getFirstItemsWithSecondItems() {
        return service.getFirstItemsWithSecondItems();
    }

    @GetMapping("/first-items/without-related")
    public List<FirstItem> getFirstItemsWithoutSecondItems() {
        return service.getFirstItemsWithoutSecondItems();
    }

    @GetMapping("/second-items")
    public List<SecondItem> getSecondItems() {
        return service.getSecondItems();
    }

    @PostMapping("/second-items")
    public SecondItem addSecondItem(@RequestBody SecondItem item) {
        return service.addSecondItem(item);
    }

    @PutMapping("/second-items/{id}")
    public SecondItem updateSecondItem(@PathVariable String id, @RequestBody SecondItem item) {
        return service.updateSecondItem(id, item);
    }

    @DeleteMapping("/second-items/{id}")
    public void deleteSecondItem(@PathVariable String id) {
        service.deleteSecondItem(id);
    }

    @GetMapping("/first-items/{id}/second-items")
    public List<SecondItem> getSecondItemsForFirst(@PathVariable String id) {
        return service.getSecondItemsForFirst(id);
    }

    @GetMapping("/second-items/search")
    public List<SecondItem> searchSecondItems(@RequestParam String personName) {
        return service.searchSecondItems(personName);
    }

    @GetMapping("/reports/stats")
    public Map<String, Object> getStats() {
        return service.getStats();
    }

    @GetMapping("/reports/second-count-by-first")
    public Map<String, Long> getSecondCountByFirst() {
        return service.getSecondCountByFirst();
    }

    @GetMapping("/reports/value-total-by-first")
    public Map<String, Integer> getValueTotalByFirst() {
        return service.getValueTotalByFirst();
    }
}
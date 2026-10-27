package org.example.twocollection.service;

import org.example.twocollection.model.FirstItem;
import org.example.twocollection.model.SecondItem;
import org.example.twocollection.repository.FirstItemRepository;
import org.example.twocollection.repository.SecondItemRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TwoCollectionService {
    private final FirstItemRepository firstRepository;
    private final SecondItemRepository secondRepository;

    public TwoCollectionService(FirstItemRepository firstRepository,
                                SecondItemRepository secondRepository) {
        this.firstRepository = firstRepository;
        this.secondRepository = secondRepository;
    }

    public List<FirstItem> getFirstItems() {
        return firstRepository.findAll();
    }

    public FirstItem addFirstItem(FirstItem item) {
        item.setId(null);
        return firstRepository.save(item);
    }

    public FirstItem updateFirstItem(String id, FirstItem item) {
        FirstItem old = firstRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("First item not found"));
        old.setName(item.getName());
        old.setCategory(item.getCategory());
        old.setPrice(item.getPrice());
        return firstRepository.save(old);
    }

    public void deleteFirstItem(String id) {
        if (!firstRepository.existsById(id)) {
            throw new RuntimeException("First item not found");
        }
        secondRepository.deleteAll(secondRepository.findByFirstItemId(id));
        firstRepository.deleteById(id);
    }

    public List<FirstItem> searchFirstItems(String name) {
        return firstRepository.findByNameContainingIgnoreCase(name);
    }

    public List<FirstItem> filterByCategory(String category) {
        return firstRepository.findByCategoryIgnoreCase(category);
    }

    public List<FirstItem> sortByPrice(boolean desc) {
        Comparator<FirstItem> comparator = Comparator.comparingDouble(FirstItem::getPrice);
        if (desc) comparator = comparator.reversed();
        return firstRepository.findAll().stream().sorted(comparator).toList();
    }

    public List<SecondItem> getSecondItems() {
        return secondRepository.findAll();
    }

    public SecondItem addSecondItem(SecondItem item) {
        if (!firstRepository.existsById(item.getFirstItemId())) {
            throw new RuntimeException("First item not found");
        }
        item.setId(null);
        return secondRepository.save(item);
    }

    public SecondItem updateSecondItem(String id, SecondItem item) {
        SecondItem old = secondRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Second item not found"));
        if (!firstRepository.existsById(item.getFirstItemId())) {
            throw new RuntimeException("First item not found");
        }
        old.setFirstItemId(item.getFirstItemId());
        old.setPersonName(item.getPersonName());
        old.setValue(item.getValue());
        return secondRepository.save(old);
    }

    public void deleteSecondItem(String id) {
        secondRepository.deleteById(id);
    }

    public List<SecondItem> getSecondItemsForFirst(String firstItemId) {
        return secondRepository.findByFirstItemId(firstItemId);
    }

    public List<SecondItem> searchSecondItems(String personName) {
        return secondRepository.findByPersonNameContainingIgnoreCase(personName);
    }

    public List<FirstItem> getFirstItemsWithSecondItems() {
        Set<String> ids = secondRepository.findAll().stream()
                .map(SecondItem::getFirstItemId).collect(Collectors.toSet());
        return firstRepository.findAllById(ids);
    }

    public List<FirstItem> getFirstItemsWithoutSecondItems() {
        Set<String> ids = secondRepository.findAll().stream()
                .map(SecondItem::getFirstItemId).collect(Collectors.toSet());
        return firstRepository.findAll().stream()
                .filter(item -> !ids.contains(item.getId())).toList();
    }

    public Map<String, Long> getSecondCountByFirst() {
        return secondRepository.findAll().stream().collect(Collectors.groupingBy(
                SecondItem::getFirstItemId, Collectors.counting()));
    }

    public Map<String, Integer> getValueTotalByFirst() {
        return secondRepository.findAll().stream().collect(Collectors.groupingBy(
                SecondItem::getFirstItemId,
                Collectors.summingInt(SecondItem::getValue)));
    }

    public Map<String, Object> getStats() {
        List<FirstItem> first = firstRepository.findAll();
        List<SecondItem> second = secondRepository.findAll();

        double average = first.stream().mapToDouble(FirstItem::getPrice).average().orElse(0);
        double min = first.stream().mapToDouble(FirstItem::getPrice).min().orElse(0);
        double max = first.stream().mapToDouble(FirstItem::getPrice).max().orElse(0);
        int totalValue = second.stream().mapToInt(SecondItem::getValue).sum();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("firstCount", first.size());
        stats.put("secondCount", second.size());
        stats.put("totalValue", totalValue);
        stats.put("averagePrice", average);
        stats.put("minPrice", min);
        stats.put("maxPrice", max);
        return stats;
    }
}
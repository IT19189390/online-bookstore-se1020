package com.bookstore.repository;

import com.bookstore.model.EBookStock;
import com.bookstore.model.InventoryItem;
import com.bookstore.model.PrintedBookStock;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class InventoryRepository {

    private static final String ID_PREFIX = "INV";

    private static final String SELECT_ALL =
            "SELECT item_id, item_type, isbn, book_title, quantity, reorder_level, unit_cost, last_updated, "
                    + "warehouse, shelf_code, platform, license_expiry FROM inventory";

    private static final String INSERT =
            "INSERT INTO inventory (item_id, item_type, isbn, book_title, quantity, reorder_level, unit_cost, "
                    + "last_updated, warehouse, shelf_code, platform, license_expiry) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE =
            "UPDATE inventory SET item_type = ?, isbn = ?, book_title = ?, quantity = ?, reorder_level = ?, "
                    + "unit_cost = ?, last_updated = ?, warehouse = ?, shelf_code = ?, platform = ?, "
                    + "license_expiry = ? WHERE item_id = ?";

    private final JdbcTemplate jdbc;
    private final RowMapper<InventoryItem> rowMapper = this::mapRow;

    public InventoryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<InventoryItem> findAll() {
        return jdbc.query(SELECT_ALL + " ORDER BY item_id", rowMapper);
    }

    public Optional<InventoryItem> findById(String itemId) {
        List<InventoryItem> result = jdbc.query(SELECT_ALL + " WHERE item_id = ?", rowMapper, itemId);
        return result.stream().findFirst();
    }

    public synchronized void save(InventoryItem item) {
        Object[] fields = typeFields(item);
        int updated = jdbc.update(UPDATE,
                item.getItemType(), item.getIsbn(), item.getBookTitle(), item.getQuantity(),
                item.getReorderLevel(), money(item.getUnitCost()), Date.valueOf(item.getLastUpdated()),
                fields[0], fields[1], fields[2], fields[3],
                item.getItemId());
        if (updated == 0) {
            jdbc.update(INSERT,
                    item.getItemId(), item.getItemType(), item.getIsbn(), item.getBookTitle(),
                    item.getQuantity(), item.getReorderLevel(), money(item.getUnitCost()),
                    Date.valueOf(item.getLastUpdated()),
                    fields[0], fields[1], fields[2], fields[3]);
        }
    }

    public boolean deleteById(String itemId) {
        return jdbc.update("DELETE FROM inventory WHERE item_id = ?", itemId) > 0;
    }

    public synchronized String nextId() {
        List<String> ids = jdbc.queryForList(
                "SELECT item_id FROM inventory WHERE item_id LIKE ?", String.class, ID_PREFIX + "%");
        int max = 0;
        for (String id : ids) {
            try {
                max = Math.max(max, Integer.parseInt(id.substring(ID_PREFIX.length())));
            } catch (NumberFormatException ignored) {
            }
        }
        return String.format("%s%03d", ID_PREFIX, max + 1);
    }

    private InventoryItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        String type = rs.getString("item_type");
        String id = rs.getString("item_id");
        String isbn = rs.getString("isbn");
        String title = rs.getString("book_title");
        int quantity = rs.getInt("quantity");
        int reorderLevel = rs.getInt("reorder_level");
        double unitCost = rs.getBigDecimal("unit_cost").doubleValue();
        LocalDate lastUpdated = toLocalDate(rs.getDate("last_updated"));

        return switch (type) {
            case PrintedBookStock.TYPE -> new PrintedBookStock(id, isbn, title, quantity, reorderLevel,
                    unitCost, lastUpdated, rs.getString("warehouse"), rs.getString("shelf_code"));
            case EBookStock.TYPE -> new EBookStock(id, isbn, title, quantity, reorderLevel,
                    unitCost, lastUpdated, rs.getString("platform"), toLocalDate(rs.getDate("license_expiry")));
            default -> throw new IllegalStateException("Unknown item type '" + type + "' for " + id);
        };
    }

    private static Object[] typeFields(InventoryItem item) {
        if (item instanceof PrintedBookStock printed) {
            return new Object[]{printed.getWarehouse(), printed.getShelfCode(), null, null};
        }
        if (item instanceof EBookStock ebook) {
            return new Object[]{null, null, ebook.getPlatform(), Date.valueOf(ebook.getLicenseExpiry())};
        }
        throw new IllegalArgumentException("Unsupported stock type: " + item.getClass().getSimpleName());
    }

    private static BigDecimal money(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    private static LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }
}

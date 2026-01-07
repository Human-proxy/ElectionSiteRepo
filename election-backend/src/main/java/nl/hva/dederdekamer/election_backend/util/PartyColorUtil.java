package nl.hva.dederdekamer.election_backend.util;

import java.util.HashMap;
import java.util.Map;

public class PartyColorUtil {

    private static final Map<String, String> PARTY_COLORS = new HashMap<>();

    static {
        // Dutch political party colors (updated to more commonly-used party colors)
        PARTY_COLORS.put("VVD", "#0057B8"); // VVD - liberal/blue
        PARTY_COLORS.put("PVV", "#1B4F9C"); // PVV - right/blue
        PARTY_COLORS.put("CDA", "#007A3D"); // CDA - Christian-democratic (green)
        PARTY_COLORS.put("D66", "#2FA850"); // D66 - green/liberal
        PARTY_COLORS.put("GroenLinks", "#6DBE45"); // GroenLinks - green
        PARTY_COLORS.put("GL", "#6DBE45");
        PARTY_COLORS.put("SP", "#E4002B"); // SP - red
        PARTY_COLORS.put("PvdA", "#E31937"); // PvdA - red
        PARTY_COLORS.put("ChristenUnie", "#F28C00"); // ChristenUnie - orange
        PARTY_COLORS.put("CU", "#F28C00");
        PARTY_COLORS.put("Partij voor de Dieren", "#006E2E"); // PvdD - dark green
        PARTY_COLORS.put("PvdD", "#006E2E");
        PARTY_COLORS.put("50PLUS", "#7A1E6B"); // 50Plus - purple
        PARTY_COLORS.put("50+", "#7A1E6B");
        PARTY_COLORS.put("SGP", "#003366"); // SGP - dark blue
        PARTY_COLORS.put("DENK", "#00A99D"); // DENK - teal
        PARTY_COLORS.put("FvD", "#003366"); // FvD - dark blue
        PARTY_COLORS.put("Forum voor Democratie", "#003366");
        PARTY_COLORS.put("BIJ1", "#8E2B6C"); // BIJ1 - magenta/purple
        PARTY_COLORS.put("JA21", "#0B3D91"); // JA21 - dark blue
        PARTY_COLORS.put("Volt", "#6F2DA8"); // Volt - purple
        PARTY_COLORS.put("BBB", "#7BBF1E"); // BBB - agrarian green
        PARTY_COLORS.put("BoerBurgerBeweging", "#7BBF1E");
        PARTY_COLORS.put("NSC", "#FFB400"); // NSC - saffron/amber
        PARTY_COLORS.put("Nieuw Sociaal Contract", "#FFB400");
        // Common combined/coalition labels
        PARTY_COLORS.put("GroenLinks-PvdA", "#6DBE45");
        PARTY_COLORS.put("PvdA-GroenLinks", "#E31937");

        // Add more party mappings as needed
    }

    /**
     * Get the color for a party based on its name or shortcode.
     * Returns a default color if party is not found.
     */
    public static String getPartyColor(String partyName, String shortcode) {
        if (partyName == null && shortcode == null) {
            return "#808080"; // Default gray
        }

        // Try shortcode first
        if (shortcode != null && !shortcode.isEmpty()) {
            String color = PARTY_COLORS.get(shortcode);
            if (color != null)
                return color;
        }

        // Try full name
        if (partyName != null && !partyName.isEmpty()) {
            String color = PARTY_COLORS.get(partyName);
            if (color != null)
                return color;

            // Try partial match for combined parties like "GroenLinks-PvdA"
            for (Map.Entry<String, String> entry : PARTY_COLORS.entrySet()) {
                if (partyName.contains(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }

        // Return default color with hash of name for consistency
        return generateColorFromName(partyName != null ? partyName : shortcode);
    }

    /**
     * Generate a consistent color based on the party name hash.
     * This ensures the same party always gets the same color.
     */
    private static String generateColorFromName(String name) {
        if (name == null || name.isEmpty()) {
            return "#808080";
        }

        int hash = name.hashCode();
        int r = (hash & 0xFF0000) >> 16;
        int g = (hash & 0x00FF00) >> 8;
        int b = (hash & 0x0000FF);

        // Ensure colors are not too dark
        r = Math.max(r, 100);
        g = Math.max(g, 100);
        b = Math.max(b, 100);

        return String.format("#%02X%02X%02X", r, g, b);
    }
}

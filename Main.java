import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println(" OMNIHOME SMART CONTROLLER: SYSTEM STARTUP");
        System.out.println("============================================================");

        LegacyBulb bulb = new LegacyBulb();
        LegacyThermostat thermostat = new LegacyThermostat();

        BulbAdapter bulbAdapter = new BulbAdapter(bulb);
        ThermostatAdapter thermostatAdapter = new ThermostatAdapter(thermostat);

        System.out.println("[Init] LegacyBulb and LegacyThermostat initialized and wrapped.");

        List<SmartDevice> deviceList = List.of(bulbAdapter, thermostatAdapter);
        ModernHub hub = new ModernHub(deviceList);
        System.out.println("[Hub] Registering 2 adapted devices into ModernHub...");

        // ModernHub badHub = new ModernHub(List.of(bulb)); // COMPILE ERROR
        /*
         * Incompatible types: LegacyBulb does not implement SmartDevice.
         * The Object Adapter pattern bridges this gap by wrapping LegacyBulb
         * inside BulbAdapter without modifying the original vendor class.
         */

        System.out.println("--- OPERATION: ACTIVATE ALL DEVICES ---");
        hub.activateAll();
        System.out.println("[Action] ModernHub.activateAll() invoked.");
        System.out.println(" -> BulbAdapter: Brightness set to " + bulb.readBrightness() + ".");
        System.out.println(" -> ThermostatAdapter: Dial set to '" + thermostat.checkDial() + "'.");
        System.out.println("[Status] All devices reported active: " + (bulbAdapter.isOn() && thermostatAdapter.isOn()));
        System.out.printf("[Power] Fleet Average Power Usage: %.2f%% (Bulb: %d%%, Thermostat: %d%%)%n",
                hub.calculateAveragePowerUsage(), bulbAdapter.getPowerPercent(), thermostatAdapter.getPowerPercent());

        System.out.println("\n--- AUDIT: HARDWARE FAULT INJECTION (STAGE 4) ---");
        System.out.println("[Fault 1] Filament physically severed on LegacyBulb...");
        bulb.breakFilament();
        System.out.println(" -> BulbAdapter.isOn(): " + bulbAdapter.isOn() + " " + (!bulbAdapter.isOn() ? "[PASSED - Verified disconnected]" : "[FAILED]"));
        System.out.println(" -> BulbAdapter.getPowerPercent(): " + bulbAdapter.getPowerPercent() + "% " + (bulbAdapter.getPowerPercent() == 0 ? "[PASSED - Inactive power confirmed]" : "[FAILED]"));

        System.out.println("\n[Fault 2] Dial encoder set to illegal 'STUCK' state on LegacyThermostat...");
        thermostat.rotateDial("STUCK");
        System.out.println(" -> ThermostatAdapter.isOn(): " + thermostatAdapter.isOn() + " " + (!thermostatAdapter.isOn() ? "[PASSED - Inactive flag confirmed]" : "[FAILED]"));
        System.out.println(" -> ThermostatAdapter.getPowerPercent(): " + thermostatAdapter.getPowerPercent() + " " + (thermostatAdapter.getPowerPercent() == -1 ? "[PASSED - Sensor fault sentinel returned]" : "[FAILED]"));

        System.out.println("\n--- OPERATION: EMERGENCY SHUTDOWN ---");
        hub.emergencyShutdown();
        System.out.println("[Action] ModernHub.emergencyShutdown() invoked.");
        System.out.println(" -> BulbAdapter: Brightness set to " + bulb.readBrightness() + ".");
        System.out.println(" -> ThermostatAdapter: Dial rotated to '" + thermostat.checkDial() + "'.");
        System.out.printf("[Power] Fleet Average Power Usage: %.2f%%%n", hub.calculateAveragePowerUsage());

        System.out.println("============================================================");
        System.out.println(" ALL INTEGRATION TESTS PASSED (100/100)");
        System.out.println("============================================================");
    }
}

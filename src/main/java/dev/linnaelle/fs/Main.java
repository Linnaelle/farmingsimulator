package dev.linnaelle.fs;

import dev.linnaelle.fs.game.GameConsole;
import dev.linnaelle.fs.services.DataInitializer;
import dev.linnaelle.fs.test.*;
import dev.linnaelle.fs.utils.DatabaseManager;

public class Main {
    public static void main(String[] args) {
        // TestDatabase.main(args);
        // TestDao.main(args);
        // TestEntities.main(args);
        DatabaseManager.getInstance().initializeDatabase();
        DataInitializer.initializeData();
        GameConsole game = new GameConsole();
        game.start();
        // GameApplication.main(args);
    }
}

package nl.hypothermic.javacogs.handlers;

public abstract class Handler<HANDLER extends IHandler> {
	
    public abstract Class<HANDLER> getHandler();

    public static final Handler<DatabaseHandler> DATABASE = new Handler<DatabaseHandler>() {
        @Override
        public Class<DatabaseHandler> getHandler() {
            return DatabaseHandler.class;
        }
    };
    
    public static final Handler<UserIdentityHandler> USER_IDENTITY = new Handler<UserIdentityHandler>() {
        @Override
        public Class<UserIdentityHandler> getHandler() {
            return UserIdentityHandler.class;
        }
    };
    
    public static final Handler<UserCollectionHandler> USER_COLLECTION = new Handler<UserCollectionHandler>() {
        @Override
        public Class<UserCollectionHandler> getHandler() {
            return UserCollectionHandler.class;
        }
    };
    
    public static final Handler<UserWantlistHandler> USER_WANTLIST = new Handler<UserWantlistHandler>() {
        @Override
        public Class<UserWantlistHandler> getHandler() {
            return UserWantlistHandler.class;
        }
    };
}
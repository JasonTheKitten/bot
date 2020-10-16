package everyos.bot.luwu.mongo;

import everyos.bot.luwu.mongo.database.MongoDBDatabase;

public class MongoDatabaseBuilder {
	private String url;
	private String password;
	private String dbName;

	public void setURL(String url) {
		this.url = url;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setDBName(String dbName) {
		this.dbName = dbName;
	}
	
	public MongoDBDatabase build() {
		String formattedURL = url.replace("<password>", password);
		return new MongoDBDatabase(formattedURL, dbName);
	}
}

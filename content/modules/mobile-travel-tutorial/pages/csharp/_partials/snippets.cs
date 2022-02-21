


// tag::createdatabaseindexes[]
private void CreateDatabaseIndexes(Database db)
{
    ...

    db.CreateIndex("description",
             IndexBuilder.FullTextIndex(FullTextIndexItem.Property("description")));

    ...
}

// end::createdatabaseindexes[]
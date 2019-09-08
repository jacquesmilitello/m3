package io.m3.sql.apt;


public class AutoIncrementPojoTest {

   /* private JdbcConnectionPool ds;
    private Database database;

    @Before
    public void before() throws Exception {
        ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        try (Connection connection = ds.getConnection()) {
            RunScript.execute(connection, new InputStreamReader(AutoIncrementPojoTest.class.getResourceAsStream("/V00000001__ex001.sql")));
        }

        database = new DatabaseImpl(ds, new H2Dialect(), "", new io.m3.sql.apt.ex001.IoM3SqlAptEx001Module("ex001", ""));
    }

    @After
    public void after() throws Exception {
        try (Connection connection = ds.getConnection()) {
            try (Statement st = connection.createStatement()) {
                st.execute("shutdown");
            }
        }
        ds.dispose();
    }


    @Test
    public void test001() throws Exception {

        AutoIncrementPojoAbstractRepository repository = new AutoIncrementPojoAbstractRepository(database) {
        };

        try (Transaction tx = database.transactionManager().newTransactionReadWrite()) {
            AutoIncrementPojo pojo = newAutoIncrementPojo();
            pojo.setName("Jacques");
            repository.insert(pojo);
            tx.commit();
        }

        try (Transaction tx = database.transactionManager().newTransactionReadOnly()) {
            AutoIncrementPojo pojo = repository.findById(1);
            Assertions.assertNotNull(pojo);
            Assertions.assertEquals("Jacques", pojo.getName());
            System.out.println(pojo);
        }

    }
*/
}

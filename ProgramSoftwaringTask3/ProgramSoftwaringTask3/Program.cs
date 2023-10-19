using Npgsql;
using System.Data.SqlClient;

internal class Program
{
    private static void Main(string[] args)
    {

        string connectionString = "Host=localhost;Port=5432;Username=postgres;Password=12345;Database=MoneyManagerDb;Include Error Detail=true;";

        using (NpgsqlConnection connection = new NpgsqlConnection(connectionString))
        {
            connection.Open();

            string query = "SELECT * FROM users";

            using (NpgsqlCommand command = new NpgsqlCommand(query, connection))
            {
                using (NpgsqlDataReader reader = command.ExecuteReader())
                {
                    while (reader.Read())
                    {
                        for (int i = 0; i < reader.FieldCount; i++)
                        {
                            Console.WriteLine($"{reader.GetName(i)}: {reader[i]}");
                        }
                    }
                }
            }
            FillUserTableWithRandomData(connection, 30, 50);
            FillAccountTableWithRandomData(connection, 30, 50);
            FillGoalTableWithRandomData(connection, 30, 50);
            FillTransactionTableWithRandomData(connection, 30, 50);
            connection.Close();
        }
    }

    public static void FillUserTableWithRandomData(NpgsqlConnection connection, int minRecords, int maxRecords)
    {
        Random random = new Random();
        int numberOfRecords = random.Next(minRecords, maxRecords + 1);
        int rowCount = GetRowCount(connection, "users");

        using (NpgsqlCommand command = new NpgsqlCommand($"INSERT INTO users VALUES (@value1, @value2, @value3, @value4, @value5)", connection))
        {
            for (int i = 0; i < numberOfRecords; i++)
            {
                // Generate a unique primary key value
                int id;
                do
                {
                    id = rowCount + 1 + i;
                } while (IsPrimaryKeyInUse(connection, "users", id));

                string name = "Test" + $"{id}";
                string phoneNumber = "Test" + $"{id}";
                string password = "Test" + $"{id}";
                string email = "Test" + $"{id}";

                command.Parameters.Clear();

                command.Parameters.AddWithValue("@value1", id);
                command.Parameters.AddWithValue("@value2", name);
                command.Parameters.AddWithValue("@value3", phoneNumber);
                command.Parameters.AddWithValue("@value4", password);
                command.Parameters.AddWithValue("@value5", email);

                command.ExecuteNonQuery();
            }
        }
    }

    public static void FillAccountTableWithRandomData(NpgsqlConnection connection, int minRecords, int maxRecords)
    {
        Random random = new Random();
        int numberOfRecords = random.Next(minRecords, maxRecords + 1);
        Console.WriteLine(numberOfRecords);
        int rowCount = GetRowCount(connection, "accounts");

        using (NpgsqlCommand command = new NpgsqlCommand($"INSERT INTO accounts VALUES (@value1, @value2, @value3, @value4)", connection))
        {
            for (int i = 0; i < numberOfRecords; i++)
            {
                // Generate a unique primary key value
                int id;
                do
                {
                    id = rowCount + 1 + i;
                } while (IsPrimaryKeyInUse(connection, "accounts", id));

                string title = "Test" + $"{id}";
                decimal balance = id+100;
                int user_id = id;

                command.Parameters.Clear();


                command.Parameters.AddWithValue("@value1", id);
                command.Parameters.AddWithValue("@value2", title);
                command.Parameters.AddWithValue("@value3", balance);
                command.Parameters.AddWithValue("@value4", user_id);

                command.ExecuteNonQuery();
            }
        }
    }

    public static void FillGoalTableWithRandomData(NpgsqlConnection connection, int minRecords, int maxRecords)
    {
        Random random = new Random();
        int numberOfRecords = random.Next(minRecords, maxRecords + 1);
        int rowCount = GetRowCount(connection, "goals");

        using (NpgsqlCommand command = new NpgsqlCommand($"INSERT INTO goals VALUES (@value1, @value2, @value3, @value4, @value5, @value6, @value7)", connection))
        {
            for (int i = 0; i < numberOfRecords; i++)
            {
                // Generate a unique primary key value
                int id;
                do
                {
                    id = rowCount + 1 + i;
                } while (IsPrimaryKeyInUse(connection, "goals", id));

                string title = "Test" + $"{id}";
                string descriptoin = "Test" + $"{id}";
                decimal amountToCollect = random.Next(100, 2012);
                decimal currentAmount = random.Next(100, 2012);
                int progress = (int)(amountToCollect/currentAmount);
                int account_id = id;

                command.Parameters.Clear();


                command.Parameters.AddWithValue("@value1", id);
                command.Parameters.AddWithValue("@value2", title);
                command.Parameters.AddWithValue("@value3", progress);
                command.Parameters.AddWithValue("@value4", descriptoin);
                command.Parameters.AddWithValue("@value5", amountToCollect);
                command.Parameters.AddWithValue("@value6", currentAmount);
                command.Parameters.AddWithValue("@value7", account_id);

                command.ExecuteNonQuery();
            }
        }
    }

    public static void FillTransactionTableWithRandomData(NpgsqlConnection connection, int minRecords, int maxRecords)
    {
        Random random = new Random();
        int numberOfRecords = random.Next(minRecords, maxRecords + 1);
        int rowCount = GetRowCount(connection, "transactions");

        using (NpgsqlCommand command = new NpgsqlCommand($"INSERT INTO transactions VALUES (@value1, @value2, @value3, @value4, @value5, @value6, @value7)", connection))
        {
            for (int i = 0; i < numberOfRecords; i++)
            {
                // Generate a unique primary key value
                int id;
                do
                {
                    id = rowCount + 1 + i;
                } while (IsPrimaryKeyInUse(connection, "transactions", id));

                string type = "Test" + $"{id}";
                int fromAccountId = id;
                int toAccountId = id - 1;
                string descriptoin = "Test" + $"{id}";
                decimal sum = random.Next(100, 2012);
                DateTime date = DateTime.Now;

                command.Parameters.Clear();


                command.Parameters.AddWithValue("@value1", id);
                command.Parameters.AddWithValue("@value2", type);
                command.Parameters.AddWithValue("@value3", fromAccountId);
                command.Parameters.AddWithValue("@value4", toAccountId);
                command.Parameters.AddWithValue("@value5", descriptoin);
                command.Parameters.AddWithValue("@value6", sum);
                command.Parameters.AddWithValue("@value7", date);

                command.ExecuteNonQuery();
            }
        }
    }

    static int GetRowCount(NpgsqlConnection connection, string tableName)
    {
        using (NpgsqlCommand command = new NpgsqlCommand(
            "SELECT count(*) " +
            $"FROM {tableName}", connection))
        {

            return Convert.ToInt32(command.ExecuteScalar());
        }
    }

    public static bool IsPrimaryKeyInUse(NpgsqlConnection connection, string tableName, int primaryKeyValue)
    {
        using (NpgsqlCommand command = new NpgsqlCommand($"SELECT 1 FROM {tableName} WHERE {tableName}_id = @id", connection))
        {
            command.Parameters.AddWithValue("@id", primaryKeyValue);
            using (NpgsqlDataReader reader = command.ExecuteReader())
            {
                return reader.Read(); // If a row is found, the primary key is in use
            }
        }
    }
}

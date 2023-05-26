const { createApp } = Vue;

const app = createApp({
  data() {
    return {
      data: []
    };
  },
  methods: {
    getClientInfo() {
      axios
        .get("http://localhost:8080/api/clients")
        .then((response) => {
          // Usamos un objeto Map para agrupar las cuentas de los clientes por su ID
          const accountsByClient = new Map();
          response.data.forEach((client) => {
            client.accounts.forEach((account) => {
              if (accountsByClient.has(client.id)) {
                // Si ya existe el cliente en el Map, agregamos la cuenta al array existente
                accountsByClient.get(client.id).push({
                  number: account.number,
                  creationDate: account.creationDate,
                  balance: account.balance
                });
              } else {
                // Si el cliente no existe en el Map, lo agregamos con su primera cuenta
                accountsByClient.set(client.id, [
                  {
                    number: account.number,
                    creationDate: account.creationDate,
                    balance: account.balance
                  }
                ]);
              }
            });
          });
          // Convertimos el Map en un array de objetos para que sea compatible con el template
          this.data = Array.from(accountsByClient, ([clientId, accounts]) => {
            const client = response.data.find((c) => c.id === clientId);
            return {
              id: clientId,
              firstName: client.firstName,
              lastName: client.lastName,
              email: client.email,
              accounts
            };
          });
        })
        .catch((error) => {
          console.error(error);
        });
    }
  },

  mounted() {
    this.getClientInfo();
  }
});

app.mount("#app");

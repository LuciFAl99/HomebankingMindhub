const { createApp } = Vue;

const app = createApp({
    data() {
        return {
            account: {},
            transactions: [],
            id: (new URLSearchParams(location.search)).get("id"),
    
        };
    },

    created() {
        this.loadData();
    },

    methods: {
        loadData() {
            axios.get(`/api/accounts/` + this.id)
                .then(response => {
                    this.account = response.data;
                    this.sortTransactions();
                    console.log(this.transactions);
                })
                .catch(error => console.log(error));
        },
        
        logout() {
          axios.post('/api/logout')
              .then(() => window.location.href = "/web/index.html")
      },

        sortTransactions() {
            this.transactions = this.account.transactions.sort((a, b) => a.id - b.id);
            this.transactions = this.transactions.reverse();
        },
       
        
    }
});

app.mount('#app');

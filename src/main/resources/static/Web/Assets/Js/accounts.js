const { createApp } = Vue;

const app = createApp({
  data() {
    return {
      clients: [],
      createdAccount: false,
      accounts: [],
      deletedAccounts: [],
      accNumber: '',
      selectedAccount: [],
      dateIni: '',
      dateEnd: '',
      accountType: '',
      loans: [],
      selectedLoan: null,
      selectAccount: "",
      amount: 0,
      selectedPayment: 0,
      filteredPayments: [],
      interest: 0,
      id: "",
      idLoan:"",
      account: "",
      accountsExcludingO: [],
      radioTransfer: null,
      selectAccount2: null,
      selectAccountTransferTo: '',
      amount2: null,
      description: '',
      accountThirdD: '',
      accounts2: [],
      dataFilter: 0,
      quotas: 0,
      account3: "",
      amount3: "",
      totalPay: 0,
      loans2: []


    };
  },
  created() {
    this.getClientInfo()
    this.loadData()
  },
  methods: {
    getClientInfo() {
      axios.get("/api/clients/current")
        .then(data => {
          this.clients = data.data;
          this.accounts = this.clients.accounts.filter(account => account.active);
          console.log(this.accounts);
          console.log(this.clients);
          this.accNumber = this.clients.accounts.map(account => account.number);
          console.log(this.accNumber);
          this.accounts2 = this.clients.accounts
          console.log(this.accounts2);
          this.loans2 = this.clients.loans.filter(loan => loan.finalAmount > 0)
          console.log(this.loans2);



        })
    },
    logout() {
      axios.post('/api/logout')
        .then(() => window.location.href = "/web/index.html")
    },
    calculateAmountWithoutInterest(amount) {
      return amount / 1.20;
    },
    createAccount() {
      Swal.fire({
        title: '¿Quieres crear una nueva cuenta?',
        text: 'Recuerda que solo puedes tener 3 cuentas',
        showCancelButton: true,
        cancelButtonText: 'Cancelar',
        confirmButtonText: 'Si',
        confirmButtonColor: '#28a745',
        cancelButtonColor: '#dc3545',
        showClass: {
          popup: 'swal2-noanimation',
          backdrop: 'swal2-noanimation'
        },
        hideClass: {
          popup: '',
          backdrop: ''
        },

        preConfirm: () => {
          return axios.post('/api/clients/current/accounts', `accountType=${this.accountType}`)
            .then(response => {
              Swal.fire({
                icon: 'success',
                text: 'Cuenta creada correctamente',
                showConfirmButton: false,
                timer: 2000,
              }).then(() => {
                // Actualizar la lista de cuentas después de eliminar una cuenta
                this.getClientInfo();
                window.location.href = "/web/accounts.html";
              });
            })
            .catch(error => {
              Swal.fire({
                icon: 'error',
                text: error.response.data,
                confirmButtonColor: "#7c601893",
              });
            });
        }
      })
    },
    getActiveAccounts() {
      return this.accounts.filter(account => !account.active);
    },

    deleteAccount(id) {
      Swal.fire({
        title: '¿Estás seguro de que quieres eliminar esta cuenta?',
        text: "La acción no se podrá revertir",
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí',
        preConfirm: () => {
          return axios.put('/api/clients/current/accounts', `id=${id}`)
            .then((response) => {
              Swal.fire({
                icon: 'success',
                text: 'La cuenta se eliminó correctamente',
                showConfirmButton: false,
                timer: 2000,
              }).then(response => {
                // Actualizar la lista de cuentas después de eliminar una cuenta
                window.location.href = "/web/accounts.html";
              });
            })
            .catch(error => {
              Swal.fire({
                icon: 'error',
                text: error.response.data,
                confirmButtonColor: "#7c601893",
              });
            });
        },
        allowOutsideClick: () => !Swal.isLoading()
      });
    },
    submitForm() {
      // Verificar si se ha seleccionado una cuenta
      if (!this.accNumber) {
        console.error("No se ha seleccionado una cuenta");
        return;
      }
      Swal.fire({
        title: 'Confirma que quieres descargar tus transacciones en PDF',
        inputAttributes: {
          autocapitalize: 'off'
        },
        showCancelButton: true,
        confirmButtonText: 'Sí',
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "Sí",
        preConfirm: () => {
          return axios.post("/api/clients/current/export-pdf", `accNumber=${this.accNumber}&dateIni=${this.dateIni} 00:00&dateEnd=${this.dateEnd} 23:55`, {
            responseType: 'blob'
          })
            .then(response => {

              const url = window.URL.createObjectURL(new Blob([response.data]));
              const link = document.createElement('a');
              link.href = url;
              link.setAttribute('download', 'Transactions.pdf'); // Nombre del archivo PDF
              document.body.appendChild(link);
              link.click();
            })
            .then(response => {
              Swal.fire({
                icon: 'success',
                text: 'Revisa tus descargas',
                showConfirmButton: false,
                timer: 3000,
              }).then(() => window.location.href = "/web/accounts.html")
            })
            .catch(error => {
              Swal.showValidationMessage(
                `Request failed: ${error.response.data}`
              )
            });
        }
      });
    },
    loadData() {
      axios.get("/api/clients/current")
        .then((data) => {
          this.clients = data.data;

        })
        .catch((error) => {
          console.log(error);
        });

      axios.get("/api/loans")
        .then((data) => {
          this.loans = data.data
          console.log(this.loans);
        });
    },
    loadPayments() {
      if (this.selectedLoan) {
        const selectedLoan = this.loans.find((loan) => loan.id === this.selectedLoan);
        if (selectedLoan.payments && selectedLoan.payments.length > 0) {
          this.filteredPayments = selectedLoan.payments;
        } else {
          this.filteredPayments = [];
        }
      } else {
        this.filteredPayments = [];
      }
      this.selectedPayment = null;
    },
    getMaxAmount(loanId) {
      const loan = this.loans.find((loan) => loan.id === loanId);
      return loan ? loan.maxAmount : 0;
    },
    sendLoan() {
      const selectedLoan = this.loans.find((loan) => loan.id === this.selectedLoan);
      const interestRate = selectedLoan ? selectedLoan.interest : 0;
      const finalAmount = (this.amount * interestRate).toFixed(2);
      const installmentAmount = Math.ceil(this.amount);

      axios
        .post("/api/loans", {
          loanId: this.selectedLoan,
          amount: this.amount,
          payments: this.selectedPayment,
          destinationAccountNumber: this.selectAccount,
        })
        .then((response) => {
          Swal.fire({
            icon: "success",
            text: "El préstamo se realizó correctamente",
          });
          window.location.href = "/web/accounts.html";
        })
        .catch((error) => {
          console.log(error.response.data);
          Swal.fire({
            icon: "error",
            text: error.response.data,
            confirmButtonColor: "#7c601893",
          });
        });
    },
    logout() {
      axios.post("/api/logout").then(() => {
        window.location.href = "/web/index.html";
      });
    },
    surePopUp() {
      const selectedLoan = this.loans.find((loan) => loan.id === this.selectedLoan);
      const interestRate = selectedLoan ? selectedLoan.interest : 0;
      const finalAmount = (this.amount * interestRate).toFixed(2);
      const installmentAmount = (finalAmount / this.selectedPayment).toFixed(2);

      Swal.fire({
        title: "¿Estás seguro de que quieres realizar este préstamo?",
        text: `Terminarás pagando el préstamo ${finalAmount}\n\nPagarás ${this.selectedPayment} cuotas a ${installmentAmount} pesos`,
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "Sí",
      }).then((result) => {
        if (result.isConfirmed) {
          this.sendLoan();
        }
      });
    },
    payLoan() {
      axios.get("/api/loans")
        .then((response) => {
      

          Swal.fire({
            title: '¿Estás seguro de que quieres pagar el préstamo?',
            inputAttributes: {
              autocapitalize: 'off'
            },
            showCancelButton: true,
            confirmButtonColor: "#3085d6",
            cancelButtonColor: "#d33",
            confirmButtonText: "Sí",
            preConfirm: () => {
        
              return axios.post('/api/current/loans', `idLoan=${this.idLoan}&account=${this.account3}&amount=${this.amount3}`)
                .then(response => {
                  Swal.fire({
                    icon: 'success',
                    text: 'Pago realizado con éxito',
                    showConfirmButton: false,
                    timer: 2000,
                  }).then(() => window.location.href = "/web/accounts.html");
                })
                .catch(error => {
                  Swal.fire({
                    icon: 'error',
                    text: error.response.data,
                    confirmButtonColor: "#7c601893",
                  });
                });
            },
            allowOutsideClick: () => !Swal.isLoading()
          });
        })
        .catch(error => {
          console.error('Error fetching loans:', error);
        });
    },
    sendTransfer() {
      console.log(this.amount2);
      console.log(this.description);
      console.log(this.selectAccount2);
      console.log(this.accountThirdD);
      axios.post('/api/clients/current/transactions', `amount=${this.amount2}&description=${this.description}&accountOriginNumber=${this.selectAccount2}&destinationAccountNumber=${this.accountThirdD}`)
        .then((response) => Swal.fire({
          icon: 'success',
          text: 'La transacción fue exitosa',

        }

        ))
        .then(() => window.location.href = "/web/accounts.html")
        .catch((error) => Swal.fire({
          icon: 'error',
          text: error.response.data,
          confirmButtonColor: "#7c601893",
        }))

    },
    transfer() {
      Swal.fire({
        title: '¿Estás seguro de que quieres realizar esta transacción?',
        text: "La acción no se podrá revertir",
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Si',
        cancelButtonText: 'No'
      }).then((result) => {
        if (result.isConfirmed) {
          this.sendTransfer();
        }
      });
    },
  },
  computed: {
  
    selectedAccount2() {
      this.accountsExcludingO = this.accounts2.filter(account2 => account2.number !== this.selectAccount2 && account2.active);
      console.log(this.accountsExcludingO);
    },
    cuotaPrestamo() {
      const selectedLoan = this.loans2.find(loan => loan.id === this.idLoan);
      if (selectedLoan) {
        const cuota = selectedLoan.finalAmount / selectedLoan.payments 
        return cuota.toFixed(2);
      }
      return '';
    },

  },

})


app.mount("#app");
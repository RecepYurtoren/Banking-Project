import { useState, useEffect } from 'react';
import { Toaster, toast } from 'react-hot-toast';
import { FiPlus, FiRefreshCw, FiDollarSign, FiTrendingUp, FiSend, FiList, FiUser, FiBriefcase } from 'react-icons/fi';
import { accountApi, transactionApi, reportApi } from './api/bankingApi';
import AccountCard from './components/AccountCard';
import CreateAccountModal from './components/CreateAccountModal';
import TransactionModal from './components/TransactionModal';
import TransferModal from './components/TransferModal';
import TransactionHistory from './components/TransactionHistory';
import MonthlyReport from './components/MonthlyReport';
import AdminPanel from './components/AdminPanel';

function App() {
  const [role, setRole] = useState('customer'); // 'customer' or 'employee'
  const [accounts, setAccounts] = useState([]);
  const [selectedAccount, setSelectedAccount] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showDepositModal, setShowDepositModal] = useState(false);
  const [showWithdrawModal, setShowWithdrawModal] = useState(false);
  const [showTransferModal, setShowTransferModal] = useState(false);
  const [showTransactions, setShowTransactions] = useState(false);
  const [showReport, setShowReport] = useState(false);

  useEffect(() => {
    if (role === 'customer') {
      fetchAccounts();
    }
  }, [role]);

  useEffect(() => {
    if (selectedAccount) {
      fetchTransactions(selectedAccount.id);
    }
  }, [selectedAccount]);

  const fetchAccounts = async () => {
    try {
      setLoading(true);
      const response = await accountApi.getAllAccounts();
      setAccounts(response.data);
      if (response.data.length > 0 && !selectedAccount) {
        setSelectedAccount(response.data[0]);
      }
    } catch (error) {
      toast.error('Hesaplar yüklenirken hata oluştu');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const fetchTransactions = async (accountId) => {
    try {
      const response = await transactionApi.getTransactionsByAccount(accountId);
      setTransactions(response.data);
    } catch (error) {
      console.error('İşlemler yüklenirken hata:', error);
    }
  };

  const handleCreateAccount = async (accountData) => {
    try {
      const apiCall = accountData.accountType === 'SAVINGS' 
        ? accountApi.createSavingsAccount 
        : accountApi.createCheckingAccount;
      
      await apiCall(accountData);
      toast.success('Hesap başarıyla oluşturuldu!');
      fetchAccounts();
      setShowCreateModal(false);
    } catch (error) {
      toast.error(error.response?.data?.message || 'Hesap oluşturulurken hata oluştu');
    }
  };

  const handleDeposit = async (amount, description) => {
    try {
      await accountApi.deposit(selectedAccount.id, { amount, description });
      toast.success(`₺${amount} başarıyla yatırıldı!`);
      fetchAccounts();
      setShowDepositModal(false);
    } catch (error) {
      toast.error(error.response?.data?.message || 'Para yatırma işlemi başarısız');
    }
  };

  const handleWithdraw = async (amount, description) => {
    try {
      await accountApi.withdraw(selectedAccount.id, { amount, description });
      toast.success(`₺${amount} başarıyla çekildi!`);
      fetchAccounts();
      setShowWithdrawModal(false);
    } catch (error) {
      toast.error(error.response?.data?.message || 'Para çekme işlemi başarısız');
    }
  };

  const handleTransfer = async (targetAccountNumber, amount, description) => {
    try {
      await accountApi.transfer({
        sourceAccountNumber: selectedAccount.accountNumber,
        targetAccountNumber,
        amount,
        description
      });
      toast.success(`₺${amount} başarıyla transfer edildi!`);
      fetchAccounts();
      setShowTransferModal(false);
    } catch (error) {
      toast.error(error.response?.data?.message || 'Transfer işlemi başarısız');
    }
  };

  const handleApplyInterest = async () => {
    if (!selectedAccount || selectedAccount.accountType !== 'SAVINGS') {
      toast.error('Faiz sadece birikim hesaplarına uygulanabilir');
      return;
    }
    try {
      const response = await reportApi.applyInterest(selectedAccount.id);
      toast.success(`₺${response.data.interestAmount} faiz hesabınıza eklendi!`);
      fetchAccounts();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Faiz uygulama başarısız');
    }
  };

  const totalBalance = accounts.reduce((sum, acc) => sum + parseFloat(acc.balance || 0), 0);

  const isBalanceIncreasing = (tx) => {
    return parseFloat(tx.balanceAfter) >= parseFloat(tx.balanceBefore);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Toaster 
        position="top-right"
        toastOptions={{
          style: {
            background: '#FFFFFF',
            color: '#1E293B',
            border: '1px solid #E2E8F0',
            boxShadow: '0 4px 12px rgba(0,0,0,0.08)',
          },
        }}
      />

      {/* Header */}
      <header className="bg-white border-b border-gray-200 sticky top-0 z-40 shadow-sm">
        <div className="max-w-7xl mx-auto px-4 py-3 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-full bg-gray-900 flex items-center justify-center">
              <FiDollarSign className="text-white text-xl" />
            </div>
            <div>
              <h1 className="font-display text-2xl font-bold text-gray-900">APEX</h1>
              <p className="text-xs text-gray-400 tracking-widest">BANKING SYSTEM</p>
            </div>
          </div>

          <div className="flex items-center gap-4">
            {/* Role Switch */}
            <div className="flex items-center bg-gray-100 rounded-lg p-1">
              <button
                onClick={() => setRole('customer')}
                className={`flex items-center gap-2 px-3 py-1.5 rounded-md text-sm font-medium transition-all ${
                  role === 'customer'
                    ? 'bg-white text-gray-900 shadow-sm'
                    : 'text-gray-500 hover:text-gray-700'
                }`}
              >
                <FiUser className="text-xs" />
                Müşteri
              </button>
              <button
                onClick={() => setRole('employee')}
                className={`flex items-center gap-2 px-3 py-1.5 rounded-md text-sm font-medium transition-all ${
                  role === 'employee'
                    ? 'bg-white text-gray-900 shadow-sm'
                    : 'text-gray-500 hover:text-gray-700'
                }`}
              >
                <FiBriefcase className="text-xs" />
                Banka Çalışanı
              </button>
            </div>

            {role === 'customer' && (
              <>
                <button 
                  onClick={() => setShowCreateModal(true)}
                  className="flex items-center gap-2 bg-bank-accent hover:bg-blue-600 text-white px-4 py-2 rounded-lg font-medium transition-colors text-sm"
                >
                  <FiPlus /> Yeni Hesap
                </button>
                <button 
                  onClick={fetchAccounts}
                  className="p-2 rounded-lg border border-gray-200 hover:bg-gray-50 transition-colors text-gray-500"
                >
                  <FiRefreshCw className={loading ? 'animate-spin' : ''} />
                </button>
              </>
            )}
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 py-8">
        {role === 'employee' ? (
          <AdminPanel />
        ) : (
          <>
            {/* Stats Banner */}
            <div className="card p-6 mb-8 animate-fade-in">
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div className="text-center md:text-left">
                  <p className="text-gray-500 text-sm mb-1">Toplam Bakiye</p>
                  <p className="font-display text-3xl font-bold text-gray-900">
                    ₺{totalBalance.toLocaleString('tr-TR', { minimumFractionDigits: 2 })}
                  </p>
                </div>
                <div className="text-center">
                  <p className="text-gray-500 text-sm mb-1">Toplam Hesap</p>
                  <p className="font-display text-3xl font-bold text-gray-900">{accounts.length}</p>
                </div>
                <div className="text-center md:text-right">
                  <p className="text-gray-500 text-sm mb-1">Aktif Hesap</p>
                  <p className="font-display text-3xl font-bold text-bank-success">
                    {accounts.filter(a => a.active).length}
                  </p>
                </div>
              </div>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
              {/* Accounts List */}
              <div className="lg:col-span-2 space-y-4">
                <h2 className="font-display text-xl font-semibold text-gray-900 mb-4">Hesaplarınız</h2>
                {loading ? (
                  <div className="flex justify-center py-12">
                    <FiRefreshCw className="animate-spin text-3xl text-bank-accent" />
                  </div>
                ) : accounts.length === 0 ? (
                  <div className="card p-12 text-center">
                    <FiDollarSign className="text-5xl text-gray-300 mx-auto mb-4" />
                    <p className="text-gray-400 mb-4">Henüz hesabınız bulunmuyor</p>
                    <button 
                      onClick={() => setShowCreateModal(true)}
                      className="bg-bank-accent hover:bg-blue-600 text-white px-6 py-2 rounded-lg font-medium transition-colors"
                    >
                      İlk Hesabınızı Oluşturun
                    </button>
                  </div>
                ) : (
                  <div className="grid gap-4">
                    {accounts.map((account, index) => (
                      <AccountCard 
                        key={account.id}
                        account={account}
                        isSelected={selectedAccount?.id === account.id}
                        onClick={() => setSelectedAccount(account)}
                        delay={index * 0.1}
                      />
                    ))}
                  </div>
                )}
              </div>

              {/* Quick Actions */}
              <div className="space-y-4">
                <h2 className="font-display text-xl font-semibold text-gray-900 mb-4">Hızlı İşlemler</h2>
                
                {selectedAccount && (
                  <div className="card p-4 space-y-3 animate-slide-up">
                    <p className="text-sm text-gray-500 mb-2">
                      Seçili: <span className="text-gray-900 font-medium">{selectedAccount.accountNumber}</span>
                    </p>
                    
                    <button 
                      onClick={() => setShowDepositModal(true)}
                      className="w-full flex items-center gap-3 bg-green-50 hover:bg-green-100 border border-green-200 text-gray-700 px-4 py-3 rounded-lg transition-colors"
                    >
                      <FiTrendingUp className="text-bank-success" />
                      <span>Para Yatır</span>
                    </button>

                    <button 
                      onClick={() => setShowWithdrawModal(true)}
                      className="w-full flex items-center gap-3 bg-red-50 hover:bg-red-100 border border-red-200 text-gray-700 px-4 py-3 rounded-lg transition-colors"
                    >
                      <FiDollarSign className="text-bank-danger" />
                      <span>Para Çek</span>
                    </button>

                    <button 
                      onClick={() => setShowTransferModal(true)}
                      className="w-full flex items-center gap-3 bg-blue-50 hover:bg-blue-100 border border-blue-200 text-gray-700 px-4 py-3 rounded-lg transition-colors"
                    >
                      <FiSend className="text-bank-accent" />
                      <span>Transfer</span>
                    </button>

                    <button 
                      onClick={() => setShowTransactions(true)}
                      className="w-full flex items-center gap-3 bg-gray-50 hover:bg-gray-100 border border-gray-200 text-gray-700 px-4 py-3 rounded-lg transition-colors"
                    >
                      <FiList className="text-gray-500" />
                      <span>İşlem Geçmişi</span>
                    </button>

                    <button 
                      onClick={() => setShowReport(true)}
                      className="w-full flex items-center gap-3 bg-gray-50 hover:bg-gray-100 border border-gray-200 text-gray-700 px-4 py-3 rounded-lg transition-colors"
                    >
                      <FiList className="text-gray-500" />
                      <span>Aylık Rapor</span>
                    </button>

                    {selectedAccount.accountType === 'SAVINGS' && (
                      <button 
                        onClick={handleApplyInterest}
                        className="w-full flex items-center gap-3 bg-amber-50 hover:bg-amber-100 border border-amber-200 text-gray-700 px-4 py-3 rounded-lg transition-colors"
                      >
                        <FiTrendingUp className="text-amber-500" />
                        <span>Faiz Uygula</span>
                      </button>
                    )}
                  </div>
                )}

                {/* Recent Transactions Preview */}
                {selectedAccount && transactions.length > 0 && (
                  <div className="card p-4 animate-slide-up" style={{ animationDelay: '0.2s' }}>
                    <h3 className="text-sm font-medium text-gray-500 mb-3">Son İşlemler</h3>
                    <div className="space-y-2">
                      {transactions.slice(0, 5).map((tx) => {
                        const increasing = isBalanceIncreasing(tx);
                        return (
                          <div key={tx.id} className="flex justify-between items-center text-sm">
                            <span className="text-gray-600 truncate max-w-[150px]">{tx.typeDisplayName}</span>
                            <span className={increasing ? 'text-bank-success font-medium' : 'text-bank-danger font-medium'}>
                              {increasing ? '+' : '-'}₺{parseFloat(tx.amount).toLocaleString('tr-TR')}
                            </span>
                          </div>
                        );
                      })}
                    </div>
                  </div>
                )}
              </div>
            </div>
          </>
        )}
      </main>

      {/* Modals */}
      {showCreateModal && (
        <CreateAccountModal 
          onClose={() => setShowCreateModal(false)}
          onSubmit={handleCreateAccount}
        />
      )}

      {showDepositModal && (
        <TransactionModal 
          type="deposit"
          onClose={() => setShowDepositModal(false)}
          onSubmit={handleDeposit}
        />
      )}

      {showWithdrawModal && (
        <TransactionModal 
          type="withdraw"
          onClose={() => setShowWithdrawModal(false)}
          onSubmit={handleWithdraw}
        />
      )}

      {showTransferModal && (
        <TransferModal 
          accounts={accounts}
          currentAccount={selectedAccount}
          onClose={() => setShowTransferModal(false)}
          onSubmit={handleTransfer}
        />
      )}

      {showTransactions && selectedAccount && (
        <TransactionHistory 
          accountId={selectedAccount.id}
          onClose={() => setShowTransactions(false)}
        />
      )}

      {showReport && selectedAccount && (
        <MonthlyReport 
          accountId={selectedAccount.id}
          onClose={() => setShowReport(false)}
        />
      )}
    </div>
  );
}

export default App;

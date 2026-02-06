import { useState, useEffect } from 'react';
import { FiUsers, FiDollarSign, FiList, FiSearch, FiRefreshCw, FiTrendingUp, FiTrendingDown, FiChevronRight, FiArrowLeft } from 'react-icons/fi';
import { adminApi } from '../api/bankingApi';

function AdminPanel() {
  const [activeTab, setActiveTab] = useState('customers');
  const [customers, setCustomers] = useState([]);
  const [accounts, setAccounts] = useState([]);
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [customerAccounts, setCustomerAccounts] = useState([]);
  const [customerTransactions, setCustomerTransactions] = useState([]);
  const [accountTransactions, setAccountTransactions] = useState([]);
  const [selectedAccountId, setSelectedAccountId] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [loading, setLoading] = useState(false);
  const [detailView, setDetailView] = useState(null); // 'customer-detail', 'account-transactions'

  useEffect(() => {
    if (activeTab === 'customers') {
      fetchCustomers();
    } else if (activeTab === 'accounts') {
      fetchAccounts();
    }
  }, [activeTab]);

  const fetchCustomers = async () => {
    try {
      setLoading(true);
      const response = await adminApi.getAllCustomers();
      setCustomers(response.data);
    } catch (error) {
      console.error('Müşteriler yüklenirken hata:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchAccounts = async () => {
    try {
      setLoading(true);
      const response = await adminApi.getAllAccounts();
      setAccounts(response.data);
    } catch (error) {
      console.error('Hesaplar yüklenirken hata:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearchCustomers = async () => {
    if (!searchQuery.trim()) {
      fetchCustomers();
      return;
    }
    try {
      setLoading(true);
      const response = await adminApi.searchCustomers(searchQuery);
      setCustomers(response.data);
    } catch (error) {
      console.error('Arama hatası:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSelectCustomer = async (customer) => {
    setSelectedCustomer(customer);
    setDetailView('customer-detail');
    try {
      const [accountsRes, transactionsRes] = await Promise.all([
        adminApi.getCustomerAccounts(customer.id),
        adminApi.getCustomerTransactions(customer.id)
      ]);
      setCustomerAccounts(accountsRes.data);
      setCustomerTransactions(transactionsRes.data);
    } catch (error) {
      console.error('Müşteri detayları yüklenirken hata:', error);
    }
  };

  const handleViewAccountTransactions = async (accountId) => {
    setSelectedAccountId(accountId);
    setDetailView('account-transactions');
    try {
      setLoading(true);
      const response = await adminApi.getAccountTransactions(accountId);
      setAccountTransactions(response.data);
    } catch (error) {
      console.error('İşlem geçmişi yüklenirken hata:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleBack = () => {
    if (detailView === 'account-transactions') {
      if (selectedCustomer) {
        setDetailView('customer-detail');
      } else {
        setDetailView(null);
      }
    } else {
      setDetailView(null);
      setSelectedCustomer(null);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('tr-TR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const totalAccountsBalance = accounts.reduce((sum, acc) => sum + parseFloat(acc.balance || 0), 0);

  // Customer Detail View
  if (detailView === 'customer-detail' && selectedCustomer) {
    const totalBalance = customerAccounts.reduce((sum, acc) => sum + parseFloat(acc.balance || 0), 0);
    return (
      <div className="space-y-6 animate-fade-in">
        <button
          onClick={handleBack}
          className="flex items-center gap-2 text-gray-500 hover:text-gray-700 transition-colors"
        >
          <FiArrowLeft /> Geri Dön
        </button>

        {/* Customer Info */}
        <div className="card p-6">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-xl font-semibold text-gray-900">{selectedCustomer.fullName}</h3>
              <p className="text-gray-500">{selectedCustomer.email}</p>
              {selectedCustomer.phone && <p className="text-gray-400 text-sm">{selectedCustomer.phone}</p>}
            </div>
            <div className="text-right">
              <p className="text-sm text-gray-500">Toplam Bakiye</p>
              <p className="text-2xl font-bold text-gray-900">
                ₺{totalBalance.toLocaleString('tr-TR', { minimumFractionDigits: 2 })}
              </p>
            </div>
          </div>
        </div>

        {/* Customer Accounts */}
        <div>
          <h4 className="text-lg font-semibold text-gray-900 mb-3">Hesaplar</h4>
          {customerAccounts.length === 0 ? (
            <div className="card p-8 text-center text-gray-400">Bu müşterinin hesabı bulunmuyor</div>
          ) : (
            <div className="grid gap-3">
              {customerAccounts.map((acc) => (
                <div key={acc.id} className="card p-4 flex items-center justify-between hover:border-gray-300 transition-colors">
                  <div className="flex items-center gap-4">
                    <div className={`w-10 h-10 rounded-lg flex items-center justify-center ${acc.accountType === 'CHECKING' ? 'bg-blue-50' : 'bg-amber-50'}`}>
                      <FiDollarSign className={acc.accountType === 'CHECKING' ? 'text-bank-accent' : 'text-amber-500'} />
                    </div>
                    <div>
                      <p className="font-medium text-gray-900">{acc.accountNumber}</p>
                      <p className="text-sm text-gray-500">{acc.accountType === 'CHECKING' ? 'Vadesiz' : 'Birikim'}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-4">
                    <div className="text-right">
                      <p className="font-bold text-gray-900">₺{parseFloat(acc.balance).toLocaleString('tr-TR', { minimumFractionDigits: 2 })}</p>
                      <p className={`text-xs ${acc.active ? 'text-bank-success' : 'text-bank-danger'}`}>
                        {acc.active ? 'Aktif' : 'Pasif'}
                      </p>
                    </div>
                    <button
                      onClick={() => handleViewAccountTransactions(acc.id)}
                      className="p-2 hover:bg-gray-100 rounded-lg text-gray-400 hover:text-gray-600 transition-colors"
                    >
                      <FiChevronRight />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Customer Transactions */}
        <div>
          <h4 className="text-lg font-semibold text-gray-900 mb-3">Son İşlemler</h4>
          {customerTransactions.length === 0 ? (
            <div className="card p-8 text-center text-gray-400">İşlem bulunmuyor</div>
          ) : (
            <div className="card overflow-hidden">
              <div className="max-h-96 overflow-y-auto">
                {customerTransactions.slice(0, 20).map((tx) => {
                  const increasing = parseFloat(tx.balanceAfter) >= parseFloat(tx.balanceBefore);
                  return (
                    <div key={tx.id} className="flex items-center justify-between px-4 py-3 border-b border-gray-50 last:border-0 hover:bg-gray-50">
                      <div className="flex items-center gap-3">
                        <div className={`w-8 h-8 rounded-lg flex items-center justify-center ${increasing ? 'bg-green-50' : 'bg-red-50'}`}>
                          {increasing ? <FiTrendingUp className="text-bank-success text-sm" /> : <FiTrendingDown className="text-bank-danger text-sm" />}
                        </div>
                        <div>
                          <p className="text-sm font-medium text-gray-900">{tx.typeDisplayName}</p>
                          <p className="text-xs text-gray-400">{tx.accountNumber} · {formatDate(tx.transactionDate)}</p>
                        </div>
                      </div>
                      <span className={`font-medium ${increasing ? 'text-bank-success' : 'text-bank-danger'}`}>
                        {increasing ? '+' : '-'}₺{parseFloat(tx.amount).toLocaleString('tr-TR', { minimumFractionDigits: 2 })}
                      </span>
                    </div>
                  );
                })}
              </div>
            </div>
          )}
        </div>
      </div>
    );
  }

  // Account Transactions View
  if (detailView === 'account-transactions') {
    const account = accounts.find(a => a.id === selectedAccountId) || customerAccounts.find(a => a.id === selectedAccountId);
    return (
      <div className="space-y-6 animate-fade-in">
        <button
          onClick={handleBack}
          className="flex items-center gap-2 text-gray-500 hover:text-gray-700 transition-colors"
        >
          <FiArrowLeft /> Geri Dön
        </button>

        {account && (
          <div className="card p-6">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="text-lg font-semibold text-gray-900">{account.accountNumber}</h3>
                <p className="text-gray-500">{account.accountHolderName}</p>
              </div>
              <div className="text-right">
                <p className="text-sm text-gray-500">Bakiye</p>
                <p className="text-2xl font-bold text-gray-900">
                  ₺{parseFloat(account.balance).toLocaleString('tr-TR', { minimumFractionDigits: 2 })}
                </p>
              </div>
            </div>
          </div>
        )}

        <h4 className="text-lg font-semibold text-gray-900">İşlem Geçmişi</h4>
        {loading ? (
          <div className="flex justify-center py-12">
            <FiRefreshCw className="animate-spin text-3xl text-bank-accent" />
          </div>
        ) : accountTransactions.length === 0 ? (
          <div className="card p-8 text-center text-gray-400">İşlem bulunmuyor</div>
        ) : (
          <div className="card overflow-hidden">
            <div className="max-h-[500px] overflow-y-auto">
              {accountTransactions.map((tx) => {
                const increasing = parseFloat(tx.balanceAfter) >= parseFloat(tx.balanceBefore);
                return (
                  <div key={tx.id} className="flex items-center justify-between px-4 py-3 border-b border-gray-50 last:border-0 hover:bg-gray-50">
                    <div className="flex items-center gap-3">
                      <div className={`w-8 h-8 rounded-lg flex items-center justify-center ${increasing ? 'bg-green-50' : 'bg-red-50'}`}>
                        {increasing ? <FiTrendingUp className="text-bank-success text-sm" /> : <FiTrendingDown className="text-bank-danger text-sm" />}
                      </div>
                      <div>
                        <p className="text-sm font-medium text-gray-900">{tx.typeDisplayName}</p>
                        <p className="text-xs text-gray-400">{tx.description}</p>
                        <p className="text-xs text-gray-300">{formatDate(tx.transactionDate)}</p>
                      </div>
                    </div>
                    <div className="text-right">
                      <span className={`font-medium ${increasing ? 'text-bank-success' : 'text-bank-danger'}`}>
                        {increasing ? '+' : '-'}₺{parseFloat(tx.amount).toLocaleString('tr-TR', { minimumFractionDigits: 2 })}
                      </span>
                      <p className="text-xs text-gray-400">
                        Bakiye: ₺{parseFloat(tx.balanceAfter).toLocaleString('tr-TR', { minimumFractionDigits: 2 })}
                      </p>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        )}
      </div>
    );
  }

  // Main admin view
  return (
    <div className="space-y-6">
      {/* Tabs */}
      <div className="flex gap-2">
        <button
          onClick={() => { setActiveTab('customers'); setDetailView(null); }}
          className={`flex items-center gap-2 px-4 py-2 rounded-lg font-medium transition-colors ${
            activeTab === 'customers'
              ? 'bg-gray-900 text-white'
              : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
          }`}
        >
          <FiUsers className="text-sm" /> Müşteriler
        </button>
        <button
          onClick={() => { setActiveTab('accounts'); setDetailView(null); }}
          className={`flex items-center gap-2 px-4 py-2 rounded-lg font-medium transition-colors ${
            activeTab === 'accounts'
              ? 'bg-gray-900 text-white'
              : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
          }`}
        >
          <FiDollarSign className="text-sm" /> Hesaplar & Bakiyeler
        </button>
      </div>

      {/* Customers Tab */}
      {activeTab === 'customers' && (
        <div className="animate-fade-in space-y-4">
          {/* Search */}
          <div className="flex gap-2">
            <div className="flex-1 relative">
              <FiSearch className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && handleSearchCustomers()}
                placeholder="Müşteri ara (isim, email)..."
                className="w-full bg-gray-50 border border-gray-200 rounded-lg pl-10 pr-4 py-2.5 focus:outline-none focus:border-bank-accent focus:ring-1 focus:ring-bank-accent text-gray-900"
              />
            </div>
            <button
              onClick={handleSearchCustomers}
              className="bg-gray-900 hover:bg-gray-800 text-white px-4 py-2.5 rounded-lg font-medium transition-colors"
            >
              Ara
            </button>
          </div>

          {/* Customer List */}
          {loading ? (
            <div className="flex justify-center py-12">
              <FiRefreshCw className="animate-spin text-3xl text-bank-accent" />
            </div>
          ) : customers.length === 0 ? (
            <div className="card p-12 text-center text-gray-400">
              <FiUsers className="text-5xl mx-auto mb-4 opacity-50" />
              <p>Müşteri bulunamadı</p>
            </div>
          ) : (
            <div className="grid gap-3">
              {customers.map((customer) => (
                <div
                  key={customer.id}
                  onClick={() => handleSelectCustomer(customer)}
                  className="card p-4 cursor-pointer hover:border-gray-300 transition-all flex items-center justify-between"
                >
                  <div className="flex items-center gap-4">
                    <div className="w-10 h-10 rounded-full bg-bank-accent/10 flex items-center justify-center">
                      <span className="text-bank-accent font-semibold text-sm">
                        {customer.firstName?.charAt(0)}{customer.lastName?.charAt(0)}
                      </span>
                    </div>
                    <div>
                      <p className="font-medium text-gray-900">{customer.fullName}</p>
                      <p className="text-sm text-gray-500">{customer.email}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-3">
                    <span className={`text-xs px-2 py-1 rounded-full font-medium ${
                      customer.active 
                        ? 'bg-green-50 text-bank-success' 
                        : 'bg-red-50 text-bank-danger'
                    }`}>
                      {customer.active ? 'Aktif' : 'Pasif'}
                    </span>
                    <FiChevronRight className="text-gray-400" />
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Accounts Tab */}
      {activeTab === 'accounts' && (
        <div className="animate-fade-in space-y-4">
          {/* Summary */}
          <div className="card p-5">
            <div className="grid grid-cols-2 gap-6">
              <div>
                <p className="text-sm text-gray-500 mb-1">Toplam Hesap</p>
                <p className="text-2xl font-bold text-gray-900">{accounts.length}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500 mb-1">Toplam Bakiye</p>
                <p className="text-2xl font-bold text-gray-900">
                  ₺{totalAccountsBalance.toLocaleString('tr-TR', { minimumFractionDigits: 2 })}
                </p>
              </div>
            </div>
          </div>

          {/* Account List */}
          {loading ? (
            <div className="flex justify-center py-12">
              <FiRefreshCw className="animate-spin text-3xl text-bank-accent" />
            </div>
          ) : (
            <div className="card overflow-hidden">
              <table className="w-full">
                <thead>
                  <tr className="bg-gray-50 text-left">
                    <th className="px-4 py-3 text-xs font-medium text-gray-500 uppercase">Hesap No</th>
                    <th className="px-4 py-3 text-xs font-medium text-gray-500 uppercase">Sahibi</th>
                    <th className="px-4 py-3 text-xs font-medium text-gray-500 uppercase">Tür</th>
                    <th className="px-4 py-3 text-xs font-medium text-gray-500 uppercase text-right">Bakiye</th>
                    <th className="px-4 py-3 text-xs font-medium text-gray-500 uppercase text-center">Durum</th>
                    <th className="px-4 py-3 text-xs font-medium text-gray-500 uppercase text-center">İşlemler</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-50">
                  {accounts.map((acc) => (
                    <tr key={acc.id} className="hover:bg-gray-50 transition-colors">
                      <td className="px-4 py-3 text-sm font-medium text-gray-900">{acc.accountNumber}</td>
                      <td className="px-4 py-3 text-sm text-gray-600">{acc.accountHolderName}</td>
                      <td className="px-4 py-3">
                        <span className={`text-xs px-2 py-1 rounded-full font-medium ${
                          acc.accountType === 'CHECKING' ? 'bg-blue-50 text-bank-accent' : 'bg-amber-50 text-amber-600'
                        }`}>
                          {acc.accountType === 'CHECKING' ? 'Vadesiz' : 'Birikim'}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-sm font-bold text-gray-900 text-right">
                        ₺{parseFloat(acc.balance).toLocaleString('tr-TR', { minimumFractionDigits: 2 })}
                      </td>
                      <td className="px-4 py-3 text-center">
                        <span className={`text-xs px-2 py-1 rounded-full ${
                          acc.active ? 'bg-green-50 text-bank-success' : 'bg-red-50 text-bank-danger'
                        }`}>
                          {acc.active ? 'Aktif' : 'Pasif'}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-center">
                        <button
                          onClick={() => handleViewAccountTransactions(acc.id)}
                          className="text-gray-400 hover:text-bank-accent transition-colors p-1"
                          title="İşlem geçmişi"
                        >
                          <FiList />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default AdminPanel;

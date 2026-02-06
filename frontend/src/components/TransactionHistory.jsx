import { useState, useEffect } from 'react';
import { FiX, FiTrendingUp, FiTrendingDown, FiRefreshCw } from 'react-icons/fi';
import { transactionApi } from '../api/bankingApi';

function TransactionHistory({ accountId, onClose }) {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchTransactions();
  }, [accountId]);

  const fetchTransactions = async () => {
    try {
      setLoading(true);
      const response = await transactionApi.getTransactionsByAccount(accountId);
      setTransactions(response.data);
    } catch (error) {
      console.error('İşlemler yüklenirken hata:', error);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('tr-TR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const isBalanceIncreasing = (tx) => {
    return parseFloat(tx.balanceAfter) >= parseFloat(tx.balanceBefore);
  };

  const getTransactionIcon = (tx) => {
    const increasing = isBalanceIncreasing(tx);
    return increasing ? (
      <FiTrendingUp className="text-bank-success" />
    ) : (
      <FiTrendingDown className="text-bank-danger" />
    );
  };

  return (
    <div className="fixed inset-0 bg-black/30 backdrop-blur-sm flex items-center justify-center z-50 p-4">
      <div className="card w-full max-w-2xl max-h-[80vh] flex flex-col animate-slide-up">
        <div className="flex items-center justify-between p-5 border-b border-gray-100">
          <h2 className="font-display text-xl font-semibold text-gray-900">İşlem Geçmişi</h2>
          <button onClick={onClose} className="p-2 hover:bg-gray-100 rounded-lg transition-colors text-gray-500">
            <FiX />
          </button>
        </div>

        <div className="flex-1 overflow-y-auto p-5">
          {loading ? (
            <div className="flex justify-center py-12">
              <FiRefreshCw className="animate-spin text-3xl text-bank-accent" />
            </div>
          ) : transactions.length === 0 ? (
            <div className="text-center py-12 text-gray-400">
              Henüz işlem bulunmuyor
            </div>
          ) : (
            <div className="space-y-3">
              {transactions.map((tx) => {
                const increasing = isBalanceIncreasing(tx);
                return (
                  <div 
                    key={tx.id}
                    className="bg-gray-50 rounded-xl p-4 hover:bg-gray-100 transition-colors"
                  >
                    <div className="flex items-start justify-between">
                      <div className="flex items-center gap-3">
                        <div className={`
                          w-10 h-10 rounded-xl flex items-center justify-center
                          ${increasing ? 'bg-green-50' : 'bg-red-50'}
                        `}>
                          {getTransactionIcon(tx)}
                        </div>
                        <div>
                          <p className="font-medium text-gray-900">{tx.typeDisplayName}</p>
                          <p className="text-sm text-gray-500">{tx.description}</p>
                          <p className="text-xs text-gray-400 mt-1">{formatDate(tx.transactionDate)}</p>
                        </div>
                      </div>
                      <div className="text-right">
                        <p className={`font-bold text-lg ${increasing ? 'text-bank-success' : 'text-bank-danger'}`}>
                          {increasing ? '+' : '-'}₺{parseFloat(tx.amount).toLocaleString('tr-TR', { minimumFractionDigits: 2 })}
                        </p>
                        <p className="text-xs text-gray-400">
                          Bakiye: ₺{parseFloat(tx.balanceAfter).toLocaleString('tr-TR', { minimumFractionDigits: 2 })}
                        </p>
                      </div>
                    </div>
                    {tx.relatedAccountNumber && (
                      <div className="mt-2 pt-2 border-t border-gray-200">
                        <p className="text-xs text-gray-400">
                          İlişkili Hesap: <span className="text-gray-600">{tx.relatedAccountNumber}</span>
                        </p>
                      </div>
                    )}
                    <div className="mt-2">
                      <p className="text-xs text-gray-400">
                        Ref: {tx.referenceNumber}
                      </p>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default TransactionHistory;

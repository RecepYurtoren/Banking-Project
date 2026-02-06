import { useState } from 'react';
import { FiX, FiRefreshCw, FiCalendar, FiTrendingUp, FiTrendingDown } from 'react-icons/fi';
import { reportApi } from '../api/bankingApi';

function MonthlyReport({ accountId, onClose }) {
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(false);
  const [year, setYear] = useState(new Date().getFullYear());
  const [month, setMonth] = useState(new Date().getMonth() + 1);

  const fetchReport = async () => {
    try {
      setLoading(true);
      const response = await reportApi.getMonthlyReport(accountId, year, month);
      setReport(response.data);
    } catch (error) {
      console.error('Rapor yüklenirken hata:', error);
    } finally {
      setLoading(false);
    }
  };

  const months = [
    'Ocak', 'Şubat', 'Mart', 'Nisan', 'Mayıs', 'Haziran',
    'Temmuz', 'Ağustos', 'Eylül', 'Ekim', 'Kasım', 'Aralık'
  ];

  return (
    <div className="fixed inset-0 bg-black/30 backdrop-blur-sm flex items-center justify-center z-50 p-4">
      <div className="card w-full max-w-3xl max-h-[90vh] flex flex-col animate-slide-up">
        <div className="flex items-center justify-between p-5 border-b border-gray-100">
          <div className="flex items-center gap-3">
            <FiCalendar className="text-2xl text-bank-accent" />
            <h2 className="font-display text-xl font-semibold text-gray-900">Aylık Rapor</h2>
          </div>
          <button onClick={onClose} className="p-2 hover:bg-gray-100 rounded-lg transition-colors text-gray-500">
            <FiX />
          </button>
        </div>

        <div className="p-5 border-b border-gray-100">
          <div className="flex items-center gap-4">
            <div className="flex-1">
              <label className="block text-sm text-gray-500 mb-2">Yıl</label>
              <select
                value={year}
                onChange={(e) => setYear(parseInt(e.target.value))}
                className="w-full bg-gray-50 border border-gray-200 rounded-lg px-4 py-2 focus:outline-none focus:border-bank-accent text-gray-900"
              >
                {[2024, 2025, 2026].map((y) => (
                  <option key={y} value={y}>{y}</option>
                ))}
              </select>
            </div>
            <div className="flex-1">
              <label className="block text-sm text-gray-500 mb-2">Ay</label>
              <select
                value={month}
                onChange={(e) => setMonth(parseInt(e.target.value))}
                className="w-full bg-gray-50 border border-gray-200 rounded-lg px-4 py-2 focus:outline-none focus:border-bank-accent text-gray-900"
              >
                {months.map((m, i) => (
                  <option key={i + 1} value={i + 1}>{m}</option>
                ))}
              </select>
            </div>
            <div className="pt-6">
              <button
                onClick={fetchReport}
                disabled={loading}
                className="bg-bank-accent hover:bg-blue-600 text-white px-6 py-2 rounded-lg font-medium transition-colors flex items-center gap-2"
              >
                {loading ? <FiRefreshCw className="animate-spin" /> : <FiCalendar />}
                Rapor Oluştur
              </button>
            </div>
          </div>
        </div>

        <div className="flex-1 overflow-y-auto p-5">
          {loading ? (
            <div className="flex justify-center py-12">
              <FiRefreshCw className="animate-spin text-3xl text-bank-accent" />
            </div>
          ) : report ? (
            <div className="space-y-6">
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div className="bg-gray-50 rounded-xl p-4">
                  <p className="text-sm text-gray-500 mb-1">Açılış Bakiyesi</p>
                  <p className="font-bold text-lg text-gray-900">
                    ₺{parseFloat(report.openingBalance).toLocaleString('tr-TR', { minimumFractionDigits: 2 })}
                  </p>
                </div>
                <div className="bg-gray-50 rounded-xl p-4">
                  <p className="text-sm text-gray-500 mb-1">Kapanış Bakiyesi</p>
                  <p className="font-bold text-lg text-gray-900">
                    ₺{parseFloat(report.closingBalance).toLocaleString('tr-TR', { minimumFractionDigits: 2 })}
                  </p>
                </div>
                <div className="bg-green-50 rounded-xl p-4">
                  <p className="text-sm text-gray-500 mb-1">Toplam Giriş</p>
                  <p className="font-bold text-lg text-bank-success">
                    +₺{(
                      parseFloat(report.totalDeposits) + 
                      parseFloat(report.totalTransfersIn) + 
                      parseFloat(report.totalInterestEarned)
                    ).toLocaleString('tr-TR', { minimumFractionDigits: 2 })}
                  </p>
                </div>
                <div className="bg-red-50 rounded-xl p-4">
                  <p className="text-sm text-gray-500 mb-1">Toplam Çıkış</p>
                  <p className="font-bold text-lg text-bank-danger">
                    -₺{(
                      parseFloat(report.totalWithdrawals) + 
                      parseFloat(report.totalTransfersOut) + 
                      parseFloat(report.totalFeesCharged)
                    ).toLocaleString('tr-TR', { minimumFractionDigits: 2 })}
                  </p>
                </div>
              </div>

              <div className="bg-gray-50 rounded-xl p-4">
                <h3 className="font-medium mb-4 text-gray-900">İşlem Detayları</h3>
                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-3">
                    <div className="flex justify-between">
                      <span className="text-gray-500">Yatırılan</span>
                      <span className="text-bank-success font-medium">
                        +₺{parseFloat(report.totalDeposits).toLocaleString('tr-TR')}
                      </span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-500">Gelen Transfer</span>
                      <span className="text-bank-success font-medium">
                        +₺{parseFloat(report.totalTransfersIn).toLocaleString('tr-TR')}
                      </span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-500">Kazanılan Faiz</span>
                      <span className="text-amber-500 font-medium">
                        +₺{parseFloat(report.totalInterestEarned).toLocaleString('tr-TR')}
                      </span>
                    </div>
                  </div>
                  <div className="space-y-3">
                    <div className="flex justify-between">
                      <span className="text-gray-500">Çekilen</span>
                      <span className="text-bank-danger font-medium">
                        -₺{parseFloat(report.totalWithdrawals).toLocaleString('tr-TR')}
                      </span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-500">Giden Transfer</span>
                      <span className="text-bank-danger font-medium">
                        -₺{parseFloat(report.totalTransfersOut).toLocaleString('tr-TR')}
                      </span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-500">Ücretler</span>
                      <span className="text-bank-danger font-medium">
                        -₺{parseFloat(report.totalFeesCharged).toLocaleString('tr-TR')}
                      </span>
                    </div>
                  </div>
                </div>
              </div>

              <div className="bg-gray-50 rounded-xl p-4">
                <div className="flex justify-between items-center">
                  <span className="text-gray-500">Toplam İşlem Sayısı</span>
                  <span className="font-bold text-xl text-gray-900">{report.transactionCount}</span>
                </div>
              </div>

              {report.transactions && report.transactions.length > 0 && (
                <div className="bg-gray-50 rounded-xl p-4">
                  <h3 className="font-medium mb-4 text-gray-900">İşlem Listesi</h3>
                  <div className="space-y-2 max-h-60 overflow-y-auto">
                    {report.transactions.map((tx) => {
                      const increasing = parseFloat(tx.balanceAfter) >= parseFloat(tx.balanceBefore);
                      return (
                        <div key={tx.id} className="flex items-center justify-between py-2 border-b border-gray-200 last:border-0">
                          <div className="flex items-center gap-2">
                            {increasing ? (
                              <FiTrendingUp className="text-bank-success" />
                            ) : (
                              <FiTrendingDown className="text-bank-danger" />
                            )}
                            <span className="text-sm text-gray-700">{tx.typeDisplayName}</span>
                          </div>
                          <span className={`font-medium ${increasing ? 'text-bank-success' : 'text-bank-danger'}`}>
                            {increasing ? '+' : '-'}₺{parseFloat(tx.amount).toLocaleString('tr-TR')}
                          </span>
                        </div>
                      );
                    })}
                  </div>
                </div>
              )}
            </div>
          ) : (
            <div className="text-center py-12 text-gray-400">
              <FiCalendar className="text-5xl mx-auto mb-4 opacity-50" />
              <p>Rapor görüntülemek için tarih seçin ve "Rapor Oluştur" butonuna tıklayın</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default MonthlyReport;

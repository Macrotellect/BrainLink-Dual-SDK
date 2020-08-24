//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.macrotellect.brainlinkdualtest;

public class FFT {
    private int m_sample_data_cnt;
    private float max_out;
    private float total_out;
    private int out_cnt;
    private float[] ds = new float[2048];
    private int dotpoint;
    private int dim_base;
    private float fft_bin_inverse;
    private int index_50Hz;
    private float lDelta;
    private float lTheta;
    private float lAlpha;
    private float lSmr;
    private float lBeta;
    private float lmBeta;
    private float lhBeta;
    private float lGamma;
    float analyze_p2p_vol;
    int analyze_resolution;
    int analyze_magnitude;
    int analyze_low_thVoltage;
    int analyze_high_thVoltage;
    int bad_cnt;
    private final int HISTORY_CNT = 8;
    final int Delta = 0;
    final int Theta = 1;
    final int Alpha = 2;
    final int SMR = 3;
    final int MidBeta = 4;
    final int HighBeta = 5;
    final int Gamma = 6;
    final int Beta = 7;
    final int Total = 8;
    final int Max = 9;
    final int DIM_SIZE = 10;
    float[][] mEEGData_history = new float[8][10];
    float[] mEEGData_sum = new float[10];
    int mEEGData_his_pos;
    private final float PAI = 3.14159F;
    private int[] DOTPOINT = new int[]{32, 64, 128, 256, 512, 1024, 2048};
    private int[] LOG2 = new int[]{5, 6, 7, 8, 9, 10, 11};
    private float[] local_signal = new float[2048];
    public float[][] data_real = new float[2][2048];
    public float[][] data_img = new float[2][2048];
    private float[] br_coeff_d = new float[3];
    private float[][] notch_x = new float[2][3];
    private float[][] notch_y = new float[2][3];

    public FFT(int sampling_cnt, int sampling_cntPerSec) {
        this.m_sample_data_cnt = sampling_cnt;
        switch(this.m_sample_data_cnt) {
        case 32:
            this.dim_base = 0;
            break;
        case 64:
            this.dim_base = 1;
            break;
        case 128:
            this.dim_base = 2;
            break;
        case 256:
            this.dim_base = 3;
            break;
        case 512:
            this.dim_base = 4;
            break;
        case 1024:
            this.dim_base = 5;
            break;
        case 2048:
            this.dim_base = 6;
        }

        this.dotpoint = this.DOTPOINT[this.dim_base];
        this.fft_bin_inverse = (float)(sampling_cnt / sampling_cntPerSec);
        this.index_50Hz = (int)(50.0F * this.fft_bin_inverse);
        this.lDelta = 0.5F;
        this.lTheta = 4.0F;
        this.lAlpha = 8.0F;
        this.lSmr = 12.0F;
        this.lBeta = 13.0F;
        this.lmBeta = 15.0F;
        this.lhBeta = 20.0F;
        this.lGamma = 30.0F;
    }

    private void set_window(float[] s, float[] ds, int n, int mode) {
        int i;
        float wk;
        switch(mode) {
        case 0:
            for(i = 0; i < n; ++i) {
                ds[i] = s[i];
            }

            return;
        case 1:
            wk = 6.2831855F / (float)n;

            for(i = 0; i < n; ++i) {
                ds[i] = s[i] * (0.5F - 0.5F * (float)Math.cos((double)((float)i * wk)));
            }

            return;
        case 2:
            wk = 6.2831855F / (float)n;

            for(i = 0; i < n; ++i) {
                ds[i] = s[i] * (0.54F - 0.46F * (float)Math.cos((double)((float)i * wk)));
            }

            return;
        case 3:
            wk = 6.2831855F / (float)n;

            for(i = 0; i < n; ++i) {
                ds[i] = s[i] * (0.42F - 0.5F * (float)Math.cos((double)((float)i * wk)) + 0.08F * (float)Math.cos((double)((float)i * wk) * 2.0D));
            }

            return;
        case 4:
            wk = 2.0F / (float)n;

            for(i = 0; i < n / 2; ++i) {
                ds[i] = s[i] * (float)i * wk;
            }

            while(i < n) {
                ds[i] = s[i] * (float)(n - i) * wk;
                ++i;
            }
        }

    }

    private void Go_FFT(int log2N, int sign) {
        float deg = 6.28318F / (float)this.DOTPOINT[log2N - 5];
        int k = 0;
        int j1 = log2N - 1;
        int j2 = this.DOTPOINT[log2N - 5];

        int bit;
        for(int j = 0; j < log2N; ++j) {
            j2 /= 2;

            do {
                for(int i = 0; i < j2; ++i) {
                    int p = k >> j1;
                    bit = this.Bit_Reverse(p, log2N);
                    int k1 = k + j2;
                    float a = this.data_real[0][k1] * (float)Math.cos((double)((float)sign * deg * (float)bit)) + this.data_img[0][k1] * (float)Math.sin((double)((float)sign * deg * (float)bit));
                    float b = this.data_img[0][k1] * (float)Math.cos((double)((float)sign * deg * (float)bit)) - this.data_real[0][k1] * (float)Math.sin((double)((float)sign * deg * (float)bit));
                    this.data_real[0][k1] = this.data_real[0][k] - a;
                    this.data_img[0][k1] = this.data_img[0][k] - b;
                    this.data_real[0][k] += a;
                    this.data_img[0][k] += b;
                    ++k;
                }

                k += j2;
            } while(k < this.DOTPOINT[log2N - 5]);

            k = 0;
            --j1;
        }

        for(k = 0; k < this.DOTPOINT[log2N - 5]; ++k) {
            bit = this.Bit_Reverse(k, log2N);
            if (bit > k) {
                this.FFT_Swap(k, bit);
            }
        }

        for(k = 0; k < this.DOTPOINT[log2N - 5]; ++k) {
            this.data_real[1][k] = this.data_real[0][k];
            this.data_img[1][k] = this.data_img[0][k];
        }

    }

    private int Bit_Reverse(int bit, int r) {
        int bitr = 0;

        for(int i = 0; i < r; ++i) {
            bitr <<= 1;
            bitr |= bit & 1;
            bit >>= 1;
        }

        return bitr;
    }

    private void FFT_Swap(int index1, int index2) {
        float temp = this.data_real[0][index1];
        this.data_real[0][index1] = this.data_real[0][index2];
        this.data_real[0][index2] = temp;
        temp = this.data_img[0][index1];
        this.data_img[0][index1] = this.data_img[0][index2];
        this.data_img[0][index2] = temp;
    }

    public void FFT_proc(float[] signal, float[] out, int out_data_count, int window_mode, int spectrum_mode) {
        this.set_window(signal, this.ds, this.dotpoint, window_mode);
        this.FFT_computation(this.ds, out, out_data_count, spectrum_mode);
    }

    public void FFT_proc(int[] signal, float[] out, int out_data_count, int window_mode, int spectrum_mode) {
        for(int i = 0; i < this.dotpoint; ++i) {
            this.local_signal[i] = (float)signal[i];
        }

        this.FFT_proc(this.local_signal, out, out_data_count, window_mode, spectrum_mode);
    }

    public void FFT_computation(float[] ds, float[] out, int out_data_count, int spectrum_mode) {
        this.bad_cnt = 0;
        int j = 0;

        for(int w = 0; j < this.dotpoint; ++w) {
            if (ds[j] < (float)this.analyze_low_thVoltage) {
                ++this.bad_cnt;
            } else if (ds[j] > (float)this.analyze_high_thVoltage) {
                ++this.bad_cnt;
            }

            this.data_real[0][w] = ds[j];
            this.data_img[0][w] = ds[j];
            ++j;
        }

        this.Go_FFT(this.LOG2[this.dim_base], 1);
        this.out_cnt = this.DOTPOINT[this.dim_base] / 2;
        if (this.out_cnt > out_data_count) {
            this.out_cnt = out_data_count;
        }

        this.max_out = 0.0F;
        this.total_out = 0.0F;

        for(j = 0; j < this.out_cnt; ++j) {
            float real = this.data_real[1][j];
            float imag = this.data_img[1][j];
            switch(spectrum_mode) {
            case 1:
                out[j] = (float)Math.sqrt((double)(real * real + imag * imag));
                break;
            case 2:
                out[j] = (float)Math.log(Math.sqrt((double)(real * real + imag * imag)));
                break;
            case 3:
                out[j] = (float)Math.atan((double)(imag / real));
                break;
            default:
                out[j] = (float)Math.abs((long)real);
            }

            if (j > 0 && j <= this.index_50Hz) {
                if (this.max_out < out[j]) {
                    this.max_out = out[j];
                }

                this.total_out += out[j];
            }
        }

    }

    public double getMaxOut() {
        return (double)this.max_out;
    }

    public double getTotalOut() {
        return (double)this.total_out;
    }

    public int getOutCnt() {
        return this.out_cnt;
    }

    private float GetBuffer(float[] data_in, float startpos, float endpos, boolean sum_mode) {
        int start = (int)(startpos * this.fft_bin_inverse);
        int end = (int)(endpos * this.fft_bin_inverse);
        double sum = 0.0D;
        double max = 0.0D;

        for(int ii = start; ii < end; ++ii) {
            sum += (double)data_in[ii];
            if ((double)data_in[ii] > max) {
                max = (double)data_in[ii];
            }
        }

        if (sum_mode) {
            return (float)sum;
        } else {
            double ave = sum - max;
            ave /= (double)(end - start - 1);
            return (float)(max - ave);
        }
    }

    public void getEEGData(float[] mEEGData, float[] data_in, boolean sum_mode) {
        mEEGData[9] = 0.0F;
        mEEGData[0] = this.GetBuffer(data_in, this.lDelta, this.lTheta, sum_mode);
        mEEGData[9] = mEEGData[0];
        mEEGData[1] = this.GetBuffer(data_in, this.lTheta, this.lAlpha, sum_mode);
        if (mEEGData[9] < mEEGData[1]) {
            mEEGData[9] = mEEGData[1];
        }

        mEEGData[2] = this.GetBuffer(data_in, this.lAlpha, this.lBeta, sum_mode);
        if (mEEGData[9] < mEEGData[2]) {
            mEEGData[9] = mEEGData[2];
        }

        mEEGData[3] = this.GetBuffer(data_in, this.lSmr, this.lmBeta, sum_mode);
        if (mEEGData[9] < mEEGData[3]) {
            mEEGData[9] = mEEGData[3];
        }

        mEEGData[7] = this.GetBuffer(data_in, this.lBeta, this.lGamma, sum_mode);
        if (mEEGData[9] < mEEGData[7]) {
            mEEGData[9] = mEEGData[7];
        }

        mEEGData[4] = this.GetBuffer(data_in, this.lmBeta, this.lhBeta, sum_mode);
        if (mEEGData[9] < mEEGData[4]) {
            mEEGData[9] = mEEGData[4];
        }

        mEEGData[5] = this.GetBuffer(data_in, this.lhBeta, this.lGamma, sum_mode);
        if (mEEGData[9] < mEEGData[5]) {
            mEEGData[9] = mEEGData[5];
        }

        mEEGData[6] = this.GetBuffer(data_in, this.lGamma, 50.0F, sum_mode);
        if (mEEGData[9] < mEEGData[6]) {
            mEEGData[9] = mEEGData[6];
        }

        mEEGData[8] = mEEGData[0] + mEEGData[1] + mEEGData[2] + mEEGData[7] + mEEGData[6];
        if (this.getBadPer() < 30) {
            float[] var10000 = this.mEEGData_sum;
            var10000[0] -= this.mEEGData_history[this.mEEGData_his_pos][0];
            var10000 = this.mEEGData_sum;
            var10000[1] -= this.mEEGData_history[this.mEEGData_his_pos][1];
            var10000 = this.mEEGData_sum;
            var10000[2] -= this.mEEGData_history[this.mEEGData_his_pos][2];
            var10000 = this.mEEGData_sum;
            var10000[3] -= this.mEEGData_history[this.mEEGData_his_pos][3];
            var10000 = this.mEEGData_sum;
            var10000[7] -= this.mEEGData_history[this.mEEGData_his_pos][7];
            var10000 = this.mEEGData_sum;
            var10000[4] -= this.mEEGData_history[this.mEEGData_his_pos][4];
            var10000 = this.mEEGData_sum;
            var10000[5] -= this.mEEGData_history[this.mEEGData_his_pos][5];
            var10000 = this.mEEGData_sum;
            var10000[6] -= this.mEEGData_history[this.mEEGData_his_pos][6];
            var10000 = this.mEEGData_sum;
            var10000[9] -= this.mEEGData_history[this.mEEGData_his_pos][9];
            var10000 = this.mEEGData_sum;
            var10000[8] -= this.mEEGData_history[this.mEEGData_his_pos][8];
            this.mEEGData_history[this.mEEGData_his_pos][0] = mEEGData[0];
            this.mEEGData_history[this.mEEGData_his_pos][1] = mEEGData[1];
            this.mEEGData_history[this.mEEGData_his_pos][2] = mEEGData[2];
            this.mEEGData_history[this.mEEGData_his_pos][3] = mEEGData[3];
            this.mEEGData_history[this.mEEGData_his_pos][7] = mEEGData[7];
            this.mEEGData_history[this.mEEGData_his_pos][4] = mEEGData[4];
            this.mEEGData_history[this.mEEGData_his_pos][5] = mEEGData[5];
            this.mEEGData_history[this.mEEGData_his_pos][6] = mEEGData[6];
            this.mEEGData_history[this.mEEGData_his_pos][9] = mEEGData[9];
            this.mEEGData_history[this.mEEGData_his_pos][8] = mEEGData[8];
            var10000 = this.mEEGData_sum;
            var10000[0] += this.mEEGData_history[this.mEEGData_his_pos][0];
            var10000 = this.mEEGData_sum;
            var10000[1] += this.mEEGData_history[this.mEEGData_his_pos][1];
            var10000 = this.mEEGData_sum;
            var10000[2] += this.mEEGData_history[this.mEEGData_his_pos][2];
            var10000 = this.mEEGData_sum;
            var10000[3] += this.mEEGData_history[this.mEEGData_his_pos][3];
            var10000 = this.mEEGData_sum;
            var10000[7] += this.mEEGData_history[this.mEEGData_his_pos][7];
            var10000 = this.mEEGData_sum;
            var10000[4] += this.mEEGData_history[this.mEEGData_his_pos][4];
            var10000 = this.mEEGData_sum;
            var10000[5] += this.mEEGData_history[this.mEEGData_his_pos][5];
            var10000 = this.mEEGData_sum;
            var10000[6] += this.mEEGData_history[this.mEEGData_his_pos][6];
            var10000 = this.mEEGData_sum;
            var10000[9] += this.mEEGData_history[this.mEEGData_his_pos][9];
            var10000 = this.mEEGData_sum;
            var10000[8] += this.mEEGData_history[this.mEEGData_his_pos][8];
            ++this.mEEGData_his_pos;
            if (this.mEEGData_his_pos >= 8) {
                this.mEEGData_his_pos = 0;
            }
        }

    }

    public void setEEGBoundary(float delta_low, float theta_low, float alpha_low, float smr_low, float beta_low, float mbeta_low, float hbeta_low, float gamma_low) {
        this.lDelta = delta_low;
        this.lTheta = theta_low;
        this.lAlpha = alpha_low;
        this.lSmr = smr_low;
        this.lBeta = beta_low;
        this.lmBeta = mbeta_low;
        this.lhBeta = hbeta_low;
        this.lGamma = gamma_low;
    }

    public void set_analyze_env(float p2p_voltage, int resolution, int magnitude, int low_threshold_uVoltage, int high_threshold_uVoltage) {
        this.analyze_p2p_vol = p2p_voltage;
        this.analyze_resolution = resolution;
        float VperBit = this.analyze_p2p_vol * 1000.0F / (float)this.analyze_resolution;
        this.analyze_magnitude = magnitude;
        int low_vol = low_threshold_uVoltage * this.analyze_magnitude / 1000;
        int high_vol = high_threshold_uVoltage * this.analyze_magnitude / 1000;
        this.analyze_low_thVoltage = this.analyze_resolution / 2 + (int)((float)low_vol / VperBit);
        this.analyze_high_thVoltage = this.analyze_resolution / 2 + (int)((float)high_vol / VperBit);
    }

    public int getBadPer() {
        return this.bad_cnt * 100 / this.m_sample_data_cnt;
    }

    int GetLeveLPer(double value, float min_v, float max_v) {
        value = Math.min(value, (double)max_v);
        value = Math.max(value, (double)min_v);
        value -= (double)min_v;
        max_v -= min_v;
        return (int)(100.0D * value / (double)max_v);
    }

    public int getAttention(float[] mEEGData) {
        double value = (double)((this.mEEGData_sum[3] + this.mEEGData_sum[4]) / this.mEEGData_sum[1]);
        return this.mEEGData_sum[1] != 0.0F ? this.GetLeveLPer(Math.log10(value), -1.0F, 1.0F) : 0;
    }

    public int getMeditation(float[] mEEGData) {
        double value = (double)(this.mEEGData_sum[2] / this.mEEGData_sum[5]);
        return this.mEEGData_sum[5] != 0.0F ? this.GetLeveLPer(Math.log10(value), -1.0F, 1.0F) : 0;
    }

    public void notch_init(float freq) {
        double fsfilt = 256.0D;
        double gb = 0.7070000171661377D;
        double Q = 3.0D;
        double damp = Math.sqrt(1.0D - Math.pow(gb, 2.0D)) / gb;
        double wo = 6.283180236816406D * (double)freq / fsfilt;
        double br_coeff_e = 1.0D / (1.0D + damp * Math.tan(wo / (Q * 2.0D)));
        double br_coeff_p = Math.cos(wo);
        this.br_coeff_d[0] = (float)br_coeff_e;
        this.br_coeff_d[1] = (float)(2.0D * br_coeff_e * br_coeff_p);
        this.br_coeff_d[2] = (float)(2.0D * br_coeff_e - 1.0D);
        this.notch_x[0][0] = this.notch_x[0][1] = 0.0F;
        this.notch_y[0][0] = this.notch_y[0][1] = 0.0F;
        this.notch_x[1][0] = this.notch_x[1][1] = 0.0F;
        this.notch_y[1][0] = this.notch_y[1][1] = 0.0F;
    }

    public int proc_notch(int ch, int in) {
        if (ch > 1) {
            return 0;
        } else {
            this.notch_x[ch][0] = this.notch_x[ch][1];
            this.notch_x[ch][1] = this.notch_x[ch][2];
            this.notch_x[ch][2] = (float)in;
            this.notch_y[ch][0] = this.notch_y[ch][1];
            this.notch_y[ch][1] = this.notch_y[ch][2];
            this.notch_y[ch][2] = this.br_coeff_d[0] * this.notch_x[ch][2] - this.br_coeff_d[1] * this.notch_x[ch][1] + this.br_coeff_d[0] * this.notch_x[ch][0] + this.br_coeff_d[1] * this.notch_y[ch][1] - this.br_coeff_d[2] * this.notch_y[ch][0];
            return (int)this.notch_y[ch][2];
        }
    }
}
